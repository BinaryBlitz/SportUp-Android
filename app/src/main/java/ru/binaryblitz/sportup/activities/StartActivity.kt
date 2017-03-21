package ru.binaryblitz.sportup.activities

import android.content.Intent
import android.os.Bundle
import android.view.View

import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.base.BaseActivity

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById(R.id.start_btn).setOnClickListener {
            startActivity(Intent(this@StartActivity, SelectCityActivity::class.java))
            finish()
        }
    }

}

