package ru.binaryblitz.SportUp.utils

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import ru.binaryblitz.SportUp.base.LocationDependentActivity
import java.io.IOException
import java.util.*

class LocationManager(val activity: LocationDependentActivity, val listener: LocationUpdateListener) {
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
        if (activity.client() == null) {
            activity.initGoogleApiClient()
            return
        }
        if (!activity.client()!!.isConnected) {
            activity.client()!!.connect()
            return
        }

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(activity.client())

        if (lastLocation != null) {
            load(lastLocation!!.latitude, lastLocation!!.longitude)
        } else {
            listener.onLocationUpdated(null, null)
            activity.onLocationError()
        }
    }
}
