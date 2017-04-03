package ru.binaryblitz.SportUp.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.*

class MapActivity : LocationDependentActivity(), OnMapReadyCallback {

    override fun onLocationUpdated(latitude: Double?, longitude: Double?) {
        selectedLocation = LatLng(latitude!!, longitude!!)
        moveCamera()
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
                        resultList = autocomplete(constraint.toString())
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
                    autocompleteClick(addresses, address)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun autocompleteClick(addresses: List<Address>, address: String) {
        val latitude = addresses[0].latitude
        val longitude = addresses[0].longitude

        selectedLocation = LatLng(latitude, longitude)
        selected = address
        moveCamera()

        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun autocomplete(input: String): ArrayList<String> {
        var conn: HttpURLConnection? = null
        val jsonResults = StringBuilder()
        try {
            val sb = PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?key=" + API_KEY + "&components=country:ru" +
                    "&input=" + URLEncoder.encode(input, "utf8")

            conn = URL(sb).openConnection() as HttpURLConnection
            val stream = InputStreamReader(conn.inputStream)

            val buff = CharArray(1024)
            var read: Int = stream.read(buff)

            while (read != -1) {
                jsonResults.append(buff, 0, read)
                read = stream.read(buff)
            }
        } catch (e: MalformedURLException) {
            return ArrayList()
        } catch (e: IOException) {
            return ArrayList()
        } finally {
            if (conn != null) {
                conn.disconnect()
            }
        }

        return parseAnswer(jsonResults)
    }

    private fun parseAnswer(jsonResults: StringBuilder): ArrayList<String> {
        LogUtil.logError(jsonResults.toString())
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
            save(geocoder)
            finish()
        }
    }

    private fun save(geocoder: Geocoder) {
        val addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1)
            CreateEventActivity.selectedLocation = addresses[0].getAddressLine(0)
            CreateEventActivity.latLng = selectedLocation
        } catch (e: IOException) {
            LogUtil.logException(e)
        }

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
    }

    private fun getCompleteAddressString(latitude: Double, lognitude: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, lognitude, 1)
            if (addresses != null) {
                if (addresses.size > 0) {
                    strAdd = addresses[0].getAddressLine(0)
                }
            }
        } catch (ignored: Exception) {
        }

        selectedLocation = LatLng(latitude, lognitude)

        selected = strAdd
        return strAdd
    }

    private fun moveCamera() {
        val cameraPosition = CameraPosition.Builder()
                .target(selectedLocation)
                .zoom(17f)
                .bearing(0f)
                .tilt(0f)
                .build()

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), object : GoogleMap.CancelableCallback {
            override fun onFinish() {
                Handler().postDelayed({
                    getCompleteAddressString(googleMap.cameraPosition.target.latitude, googleMap.cameraPosition.target.longitude)
                }, 50)
            }

            override fun onCancel() {}
        })
    }

    companion object {
        lateinit var selectedLocation: LatLng
        var selected = ""

        private val PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place"
        private val TYPE_AUTOCOMPLETE = "/autocomplete"
        private val OUT_JSON = "/json"
        private val API_KEY = "AIzaSyAleTZalgq4WoXgb1aAaiAD2-GK3WSGoSY"
    }
}
