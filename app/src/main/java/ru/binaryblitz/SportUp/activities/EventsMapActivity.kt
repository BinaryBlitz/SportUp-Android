package ru.binaryblitz.SportUp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_events_map.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.LocationDependentActivity
import ru.binaryblitz.SportUp.custom.CustomMapFragment
import ru.binaryblitz.SportUp.models.Event
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.Animations
import ru.binaryblitz.SportUp.utils.DateUtils
import java.util.*
import javax.inject.Inject

class EventsMapActivity : LocationDependentActivity(), CustomMapFragment.TouchableWrapper.UpdateMapAfterUserInteraction, OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var isEventOpened = false

    private val markers = HashMap<LatLng, Int>()

    @Inject
    lateinit var api: EndpointsService

    val EXTRA_COLOR = "color"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_map)
        dependencies()!!.inject(this)

        initToolbar()
        initGoogleApiClient()
        initMap()
        setOnClickListeners()
    }

    private fun initMap() {
        val map = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        Handler().post { map.getMapAsync(this@EventsMapActivity) }
    }

    private fun initToolbar() {
        appBarView.setBackgroundColor(intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR))
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

        hideBtn.setOnClickListener {
            isEventOpened = false
            Animations.animateRevealHide(cardView)
        }
    }

    override fun onUpdateMapAfterUserInteraction() {
        hideEventInformation()
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
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.uiSettings?.isMapToolbarEnabled = false

        googleMap?.setOnMarkerClickListener { marker ->
            val id = markers[marker!!.position]
            loadEvent(id!!)
            false
        }

        googleMap?.setOnMapClickListener {
            hideEventInformation()
        }

        onLoaded()
    }

    private fun hideEventInformation() {
        if (isEventOpened) {
            isEventOpened = false
            Animations.animateRevealHide(cardView)
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().post({
            if (googleApiClient!!.isConnected) {
                checkPermissions()
            } else {
                googleApiClient?.connect()
            }
        })
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

    fun onLoaded() {
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pins_footballmid)

        for (event in SportEventsActivity.eventsCollection) {
            markers.put(LatLng (event.latitude, event.longitude), event.id)
            googleMap?.addMarker(MarkerOptions()
                    .position(LatLng (event.latitude, event.longitude))
                    .icon(icon))
        }
    }

    private fun loadEvent(id: Int) {
        for (event in SportEventsActivity.eventsCollection) {
            if (event.id == id) {
                showEvent(event)
                break
            }
        }
    }

    private fun showEvent(event: Event) {
        Animations.animateRevealShow(cardView, this)
        isEventOpened = true

        name.text = event.name
        address.text = event.address
        startsAt.text = DateUtils.getTimeStringRepresentation(event.startsAt)
        userLimit.text = event.userLimit.toString() + " / " + event.teamLimit.toString()
        price.text = event.price.toString() + getString(R.string.ruble_sign)

        isPublic.visibility = if (event.isPublic) View.VISIBLE else View.GONE
    }
}

