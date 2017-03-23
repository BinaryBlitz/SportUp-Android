package ru.binaryblitz.sportup.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_events_map.*
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.base.LocationDependentActivity
import ru.binaryblitz.sportup.custom.CustomMapFragment
import ru.binaryblitz.sportup.models.MapEvent
import ru.binaryblitz.sportup.presenters.EventMapPresenter
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.utils.LogUtil
import java.util.*
import javax.inject.Inject

class EventsMapActivity : LocationDependentActivity(), CustomMapFragment.TouchableWrapper.UpdateMapAfterUserInteraction, OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    val EXTRA_ID = "id"

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_map)
        dependencies()!!.inject(this)

        initGoogleApiClient()
        initMap()
        setOnClickListeners()

        load()
    }

    private fun initMap() {
        val mMap = supportFragmentManager
                .findFragmentById(R.id.scroll) as SupportMapFragment

        Handler().post { mMap.getMapAsync(this@EventsMapActivity) }
    }

    override fun onLocationUpdated(latitude: Double?, longitude: Double?) {
        moveCamera(latitude, longitude)
    }

    override fun onLocationPermissionGranted() {
        googleMap?.isMyLocationEnabled = true
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }

        nearBtn.setOnClickListener { checkPermissions() }
    }

    override fun onUpdateMapAfterUserInteraction() {
    }

    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
        setUpMap()
    }

    @SuppressLint("NewApi")
    private fun checkLocationAccess(): Boolean {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    private fun setUpMap() {
        if (checkLocationAccess()) {
            return
        }

        googleMap?.isMyLocationEnabled = true
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            if (googleApiClient!!.isConnected) {
                checkPermissions()
            } else {
                googleApiClient?.connect()
            }
        }, 50)
    }

    private fun moveCamera(latitude: Double?, longitude: Double?) {
        val cameraPosition = CameraPosition.Builder()
                .target(LatLng(latitude!!, longitude!!))
                .zoom(17f)
                .bearing(0f)
                .tilt(0f)
                .build()

        googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun onLoaded(collection: ArrayList<MapEvent>) {
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pins_regbimid)

        for ((_, latitude, longitude) in collection) {
            googleMap?.addMarker(MarkerOptions()
                    .position(LatLng (latitude, longitude))
                    .icon(icon))
        }
    }

    private fun load() {
        val presenter = EventMapPresenter(api, this)
        presenter.getMapEvents(intent.getIntExtra(EXTRA_ID, 0))
    }
}

