package ru.binaryblitz.SportUp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_events_map.*
import kotlinx.android.synthetic.main.dialog_password.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.LocationDependentActivity
import ru.binaryblitz.SportUp.custom.CustomMapFragment
import ru.binaryblitz.SportUp.models.Event
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.*
import java.util.*
import javax.inject.Inject

class EventsMapActivity : LocationDependentActivity(), CustomMapFragment.TouchableWrapper.UpdateMapAfterUserInteraction, OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var isEventOpened = false
    private var isDialogOpened = false
    private val markers = HashMap<LatLng, Int>()

    private lateinit var icon: BitmapDescriptor

    @Inject
    lateinit var api: EndpointsService

    val EXTRA_ID = "id"
    val EXTRA_COLOR = "color"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    internal var target: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            if (SportEventsActivity.eventsCollection.size == 0) {
                return
            }

            icon = BitmapDescriptorFactory.fromBitmap(
                    SportTypesUtil.getMarker(SportEventsActivity.eventsCollection[0].sportTypeId, this@EventsMapActivity, bitmap))
            onLoaded()
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {

        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_map)
        dependencies()!!.inject(this)

        initToolbar()
        initGoogleApiClient()
        initMap()
        setOnClickListeners()

        if (SportEventsActivity.eventsCollection.size == 0) {
            return
        }

        Picasso.with(this)
                .load(SportTypesUtil.findIcon(this, SportEventsActivity.eventsCollection[0].sportTypeId))
                .into(target)
    }

    private fun openEvent(eventId: Int) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra(EXTRA_ID, eventId)
        intent.putExtra(EXTRA_COLOR, SportEventsActivity.color)
        startActivity(intent)
    }

    fun showPasswordDialog(password: String, eventId: Int) {
        Handler().post {
            isDialogOpened = true
            Animations.animateRevealShow(findViewById(R.id.dialog_new_order), this@EventsMapActivity)
            passwordButton.setOnClickListener {
                if (password == passwordEdit.text.toString()) {
                    openEvent(eventId)
                } else {
                    passwordEdit.setError(getString(R.string.wrong_password), null)
                }
            }
        }
    }

    private fun initMap() {
        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        Handler().post { map.getMapAsync(this@EventsMapActivity) }
    }

    private fun initToolbar() {
        val color = intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
        appBarView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
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

        if (event.price == 0) {
            price.setTextColor(SportEventsActivity.color)
            price.text = getString(R.string.free)
        } else {
            price.text = event.price.toString() + getString(R.string.ruble_sign)
        }

        isPublic.visibility = if (event.isPublic) View.GONE else View.VISIBLE

        cardView.setOnClickListener {
            if (event.password != null) {
                showPasswordDialog(event.password, event.id)
                return@setOnClickListener
            }
            if (!AppConfig.checkIfUserLoggedIn(this@EventsMapActivity)) {
                return@setOnClickListener
            }

            openEvent(event.id)
        }
    }
}

