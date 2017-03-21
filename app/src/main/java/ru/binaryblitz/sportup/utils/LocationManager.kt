package ru.binaryblitz.sportup.utils

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import ru.binaryblitz.sportup.activities.SelectCityActivity
import java.io.IOException
import java.util.*

class LocationManager(val activity: SelectCityActivity, val listener: LocationUpdateListener) {
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
        if (activity.getGoogleApiClient() == null) {
            activity.initGoogleApiClient()
            return
        }
        if (!activity.getGoogleApiClient()!!.isConnected) {
            activity.getGoogleApiClient()!!.connect()
            return
        }

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(activity.getGoogleApiClient())

        if (lastLocation != null) {
            load(lastLocation!!.latitude, lastLocation!!.longitude)
        } else {
            listener.onLocationUpdated(null, null)
            activity.onLocationError()
        }
    }
}