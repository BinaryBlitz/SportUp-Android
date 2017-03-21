package ru.binaryblitz.sportup.base

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import ru.binaryblitz.sportup.R

open class BaseActivity : AppCompatActivity() {

    fun onInternetConnectionError() {
        Snackbar.make(findViewById(R.id.main), R.string.lost_connection, Snackbar.LENGTH_SHORT).show()
    }

    fun onLocationError() {
        Snackbar.make(findViewById(R.id.main), R.string.location_error, Snackbar.LENGTH_SHORT).show()
    }
}
