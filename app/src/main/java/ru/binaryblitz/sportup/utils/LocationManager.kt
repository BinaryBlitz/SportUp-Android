package ru.binaryblitz.sportup.utils

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import ru.binaryblitz.sportup.base.BaseActivity
import java.io.IOException
import java.util.*

class LocationManager(val activity: BaseActivity, val googleApiClient: GoogleApiClient?, val listener: LocationUpdateListener) {
    private var lastLocation: Location? = null

    interface LocationUpdateListener {
        fun onLocationUpdated(latitude: Double?, longitude: Double?)
    }

    private fun load(latitude: Double, longitude: Double) {
        val gcd = Geocoder(activity, Locale.getDefault())
        val addresses: List<Address>
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                listener.onLocationUpdated(addresses[0].latitude, addresses[0].longitude)
            } else {
                listener.onLocationUpdated(null, null)
                activity.onLocationError()
            }
        } catch (e: IOException) {
            LogUtil.logException(e)
        }
    }

    fun getLocation() {
        if (!googleApiClient!!.isConnected) {
            googleApiClient.connect()
            listener.onLocationUpdated(null, null)
            return
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.onLocationError()
            listener.onLocationUpdated(null, null)
            return
        }

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)

        if (lastLocation != null) {
            load(lastLocation!!.latitude, lastLocation!!.longitude)
        } else {
            listener.onLocationUpdated(null, null)
            activity.onLocationError()
        }
    }
}