package ru.binaryblitz.SportUp.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import ru.binaryblitz.SportUp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.fragments.SportsListFragment
import ru.binaryblitz.SportUp.fragments.UserEventsFragment

class SportsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container, SportsListFragment()).commit()

        bottomBar.setOnTabSelectListener { tabId ->
            when (tabId) {
                R.id.tab_sports -> {
                    switchFragment(SportsListFragment())
                }
                R.id.tab_friends -> {
                    switchFragment(UserEventsFragment())
                }
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
