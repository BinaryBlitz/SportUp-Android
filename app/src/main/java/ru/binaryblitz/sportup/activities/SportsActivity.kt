package ru.binaryblitz.sportup.activities

import android.os.Bundle
import ru.binaryblitz.sportup.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.binaryblitz.sportup.R


class SportsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar.setOnTabSelectListener { tabId ->
//            if (tabId == R.id.tab_favorites) {
//            }
        }
    }
}
