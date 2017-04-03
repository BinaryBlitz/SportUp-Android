package ru.binaryblitz.SportUp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.custom.CustomMapFragment
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.LogUtil
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.*

class MapActivity : BaseActivity(), CustomMapFragment.TouchableWrapper.UpdateMapAfterUserInteraction, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private lateinit var googleMap: GoogleMap

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null

    private lateinit var searchBox: AutoCompleteTextView

    internal inner class GooglePlacesAutocompleteAdapter(context: Context, textViewResourceId: Int) : ArrayAdapter<Any>(context, textViewResourceId), Filterable {
        private var resultList: ArrayList<String>? = null

        override fun getCount(): Int {
            return resultList!!.size
        }

        override fun getItem(index: Int): String? {
            return resultList!![index]
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                    val filterResults = Filter.FilterResults()
                    if (constraint != null) {
                        resultList = autocomplete(constraint.toString())
                        filterResults.values = resultList
                        filterResults.count = if (resultList == null) 0 else resultList!!.size
                    }
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence, results: Filter.FilterResults?) {
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

        initAutocomplete()
        initMap()
        initGoogleApiClient()
        setOnClickListeners()
    }

    private fun initAutocomplete() {
        searchBox = findViewById(R.id.search_box) as AutoCompleteTextView

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
        moveCamera(false)

        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun autocomplete(input: String): ArrayList<String>? {
        var conn: HttpURLConnection? = null
        val jsonResults = StringBuilder()
        try {
            val sb = PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?key=" + API_KEY + "&components=country:ru" +
                    "&input=" + URLEncoder.encode(input, "utf8")

            conn = URL(sb).openConnection() as HttpURLConnection
            val stream = InputStreamReader(conn.inputStream)

            var read: Int = 0
            val buff = CharArray(1024)
            while (read != -1) {
                read = stream.read(buff)
                jsonResults.append(buff, 0, read)
            }
        } catch (e: MalformedURLException) {
            return null
        } catch (e: IOException) {
            return null
        } finally {
            if (conn != null) {
                conn.disconnect()
            }
        }

        return parseAnswer(jsonResults)
    }

    private fun parseAnswer(jsonResults: StringBuilder): ArrayList<String>? {
        var resultList: ArrayList<String>? = null
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

    private fun initGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return
        }

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
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
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkPermission()) {
                ActivityCompat.requestPermissions(this@MapActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
            } else {
                setUpMap()
            }
        } catch (e: Exception) {
            LogUtil.logException(e)
        }

    }

    @SuppressLint("NewApi")
    private fun checkPermission(): Boolean {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        googleMap.isMyLocationEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = true
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

    override fun onUpdateMapAfterUserInteraction() {
    }

    public override fun onResume() {
        super.onResume()
        Handler().post({
            if (mGoogleApiClient!!.isConnected) {
                getLocation()
            } else {
                mGoogleApiClient!!.connect()
            }
        })
    }

    override fun onConnected(bundle: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
                } else {
                    getLocation()
                }
            } catch (e: Exception) {
                LogUtil.logException(e)
            }

        } else {
            getLocation()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onLocationError()
            return
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLastLocation != null) {
            selectedLocation = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
            moveCamera(true)
        } else {
            onLocationError()
        }
    }

    private fun moveCamera(setText: Boolean) {
        val cameraPosition = CameraPosition.Builder()
                .target(selectedLocation)
                .zoom(17f)
                .bearing(0f)
                .tilt(0f)
                .build()

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), object : GoogleMap.CancelableCallback {
            override fun onFinish() {
                Handler().postDelayed({
                    if (setText) {
                        getCompleteAddressString(googleMap.cameraPosition.target.latitude, googleMap.cameraPosition.target.longitude)
                        searchBox.setText(selected)
                    }
                    searchBox.dismissDropDown()
                }, 50)
            }

            override fun onCancel() {}
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    onLocationError()
                }
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        onLocationError()
    }

    companion object {
        private val LOCATION_PERMISSION = 1

        lateinit var selectedLocation: LatLng
        var selected = ""

        private val PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place"
        private val TYPE_AUTOCOMPLETE = "/autocomplete"
        private val OUT_JSON = "/json"
        private val API_KEY = "AIzaSyC48bjEMes05K8RgTG6PrwVSKicZqXZ7WY"
    }
}
