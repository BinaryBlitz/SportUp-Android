package ru.binaryblitz.sportup.activities

import android.os.Bundle
import ru.binaryblitz.sportup.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.fragments.SportsListFragment


class SportsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container, SportsListFragment()).commit()

        bottomBar.setOnTabSelectListener { tabId ->
            // TODO
        }
    }
}
