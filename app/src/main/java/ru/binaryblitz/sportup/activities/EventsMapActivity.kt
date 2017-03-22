package ru.binaryblitz.sportup.activities

import android.os.Bundle
import android.os.Handler
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.base.LocationDependentActivity
import ru.binaryblitz.sportup.custom.CustomMapFragment

class EventsMapActivity : LocationDependentActivity(), CustomMapFragment.TouchableWrapper.UpdateMapAfterUserInteraction, OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_map)
    }

    override fun onLocationUpdated(latitude: Double?, longitude: Double?) {
        moveCamera(latitude, longitude)
    }

    override fun onUpdateMapAfterUserInteraction() {
    }

    override fun onMapReady(p0: GoogleMap?) {

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
}

