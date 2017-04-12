package ru.binaryblitz.SportUp.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.json.JSONException
import org.json.JSONObject
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.LocationDependentActivity
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.LogUtil
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class MapActivity : LocationDependentActivity(), OnMapReadyCallback {
    val EXTRA_EDIT = "edit"

    override fun onLocationUpdated(latitude: Double?, longitude: Double?) {
        selectedLocation = LatLng(latitude!!, longitude!!)
        moveCamera(false)
    }

    override fun onLocationPermissionGranted() {
        googleMap.isMyLocationEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = true
    }

    private lateinit var googleMap: GoogleMap

    internal inner class GooglePlacesAutocompleteAdapter(context: Context, textViewResourceId: Int) : ArrayAdapter<Any>(context, textViewResourceId), Filterable {
        private var resultList: ArrayList<String> = ArrayList()

        override fun getCount(): Int {
            return resultList.size
        }

        override fun getItem(index: Int): String? {
            return resultList[index]
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                    val filterResults = Filter.FilterResults()
                    if (constraint != null) {
                        resultList = autocompleteInput(constraint.toString())
                        filterResults.values = resultList
                        filterResults.count = resultList.size
                    }
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged()
                    } else {
                        notifyDataSetInvalidated()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initGoogleApiClient()
        initAutocomplete()
        initMap()
        setOnClickListeners()
    }

    private fun initAutocomplete() {
        searchBox.setAdapter(GooglePlacesAutocompleteAdapter(this, R.layout.list_item))
        searchBox.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val geocoder = Geocoder(this@MapActivity)
            val addresses: List<Address>
            try {
                val address = searchBox.adapter.getItem(i) as String
                addresses = geocoder.getFromLocationName(address, 1)
                if (addresses.isNotEmpty()) {
                    autocompleteClick(addresses[0].latitude, addresses[0].longitude, address)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun autocompleteClick(latitude: Double, longitude: Double, address: String) {
        selectedLocation = LatLng(latitude, longitude)
        selected = address
        moveCamera(true)

        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun autocompleteInput(input: String): ArrayList<String> {
        var connection: HttpURLConnection? = null
        val jsonResults: StringBuilder

        try {
            connection = URL("$AUTOCOMPLETE_BASE$OUT_JSON?key=$API_KEY&components=country:ru&input=" + URLEncoder.encode(input, "utf8"))
                    .openConnection() as HttpURLConnection
            jsonResults = streamToString(InputStreamReader(connection.inputStream))
        } catch (e: Exception) {
            return ArrayList()
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
        }

        return parseAnswer(jsonResults)
    }

    private fun streamToString(stream: InputStreamReader): StringBuilder {
        val jsonResults = StringBuilder()
        val buff = CharArray(1024)
        var read: Int = stream.read(buff)

        while (read != -1) {
            jsonResults.append(buff, 0, read)
            read = stream.read(buff)
        }

        return jsonResults
    }

    private fun parseAnswer(jsonResults: StringBuilder): ArrayList<String> {
        var resultList: ArrayList<String> = ArrayList()
        try {
            val jsonObj = JSONObject(jsonResults.toString())
            val prevJsonArray = jsonObj.getJSONArray("predictions")

            resultList = ArrayList<String>(prevJsonArray.length())
            (0..prevJsonArray.length() - 1).mapTo(resultList) { prevJsonArray.getJSONObject(it).getString("description") }
        } catch (e: JSONException) {
            LogUtil.logException(e)
        }

        return resultList
    }

    private fun initMap() {
        val mMap = supportFragmentManager.findFragmentById(R.id.scroll) as SupportMapFragment

        Handler().post { mMap.getMapAsync(this@MapActivity) }
    }

    private fun setOnClickListeners() {
        findViewById(R.id.back_btn).setOnClickListener { finish() }

        findViewById(R.id.add_btn).setOnClickListener {
            val geocoder: Geocoder = Geocoder(this@MapActivity, Locale.getDefault())
            if (save(geocoder)) {
                finish()
            }
        }
    }

    private fun save(geocoder: Geocoder): Boolean {
        val addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1)
            CreateEventActivity.selectedLocation = addresses[0].getAddressLine(0)
            CreateEventActivity.latLng = selectedLocation
        } catch (e: Exception) {
            Snackbar.make(main, getString(R.string.wrong_location), Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setUpMap()
        checkPermissions()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        googleMap.setPadding(0, AndroidUtilities.convertDpToPixel(66f, this).toInt(), 0, 0)

        googleMap.setOnMapClickListener(GoogleMap.OnMapClickListener { latLng ->
            if (latLng == null) {
                return@OnMapClickListener
            }

            updateMap(latLng)
        })
    }

    private fun updateMap(latLng: LatLng) {
        getCompleteAddressString(googleMap.cameraPosition.target.latitude, googleMap.cameraPosition.target.longitude)
        searchBox.setText(selected)
        searchBox.dismissDropDown()
        googleMap.clear()
        addMarker(latLng.latitude, latLng.longitude)
    }

    private fun getCompleteAddressString(latitude: Double, lognitude: Double): String {
        var addressString = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, lognitude, 1) ?: return ""
            if (addresses.size > 0) {
                addressString = addresses[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            selected = ""
        }

        selectedLocation = LatLng(latitude, lognitude)

        selected = addressString
        return addressString
    }

    private fun moveCamera(isMarkerAdded: Boolean) {
        val cameraPosition = CameraPosition.Builder()
                .target(selectedLocation)
                .zoom(17f)
                .bearing(0f)
                .tilt(0f)
                .build()

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                object : GoogleMap.CancelableCallback {
                    override fun onFinish() {
                        Handler().postDelayed({
                            getCompleteAddressString(googleMap.cameraPosition.target.latitude,
                                    googleMap.cameraPosition.target.longitude)
                            if (isMarkerAdded) {
                                addMarker(googleMap.cameraPosition.target.latitude,
                                        googleMap.cameraPosition.target.longitude)
                            }
                        }, 50)
                    }

                    override fun onCancel() { }
                }
        )
    }

    private fun addMarker(latitude: Double, longitude: Double) {
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pins_footballmid)

        googleMap.addMarker(MarkerOptions()
                .position(LatLng (latitude, longitude))
                .icon(icon))
    }

    companion object {
        lateinit var selectedLocation: LatLng
        var selected = ""

        private val AUTOCOMPLETE_BASE = "https://maps.googleapis.com/maps/api/place/autocomplete"
        private val OUT_JSON = "/json"
        private val API_KEY = "AIzaSyAleTZalgq4WoXgb1aAaiAD2-GK3WSGoSY"
    }
}
