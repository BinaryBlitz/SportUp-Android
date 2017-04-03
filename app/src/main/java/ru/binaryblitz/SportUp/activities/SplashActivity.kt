package ru.binaryblitz.SportUp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.server.DeviceInfoStore

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Fabric.with(this, Crashlytics())

        if (DeviceInfoStore.getCity(this) == "null") {
            openActivity(StartActivity::class.java)
        } else {
            openActivity(MainActivity::class.java)
        }
    }

    private fun openActivity(activity: Class<out Activity>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        finish()
    }
}

