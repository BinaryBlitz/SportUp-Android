package ru.binaryblitz.SportUp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Fabric.with(this, Crashlytics())

        findViewById(R.id.start_btn).setOnClickListener {
            startActivity(Intent(this@StartActivity, SelectCityActivity::class.java))
            finish()
        }
    }

}
