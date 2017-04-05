package ru.binaryblitz.SportUp.base

import android.Manifest
import android.os.Build
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import ru.binaryblitz.SportUp.utils.LocationManager

abstract class LocationDependentActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected var googleApiClient: GoogleApiClient? = null

    lateinit var locationManager: LocationManager

    abstract protected fun onLocationUpdated(latitude: Double?, longitude: Double?)

    abstract protected fun onLocationPermissionGranted()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = LocationManager(this,
                object : LocationManager.LocationUpdateListener {
                    override fun onLocationUpdated(latitude: Double?, longitude: Double?) {
                        this@LocationDependentActivity.onLocationUpdated(latitude, longitude)
                    }
                })
    }

    fun client(): GoogleApiClient? {
        return googleApiClient
    }

    fun initGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }
    }

    protected fun checkPermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        onLocationPermissionGranted()
                        locationManager.getLocation()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        onLocationError()
                    }
                })
                .check()
    }

    override fun onConnected(bundle: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions()
        } else {
            locationManager.getLocation()
        }
    }

    override fun onConnectionSuspended(i: Int) {
        googleApiClient?.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        onLocationError()
    }
}
