package ru.binaryblitz.SportUp.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import ru.binaryblitz.SportUp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.fragments.ProfileFragment
import ru.binaryblitz.SportUp.fragments.SportsListFragment
import ru.binaryblitz.SportUp.fragments.UserEventsFragment

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar.setOnTabSelectListener { tabId ->
            when (tabId) {
                R.id.tab_sports -> {
                    switchFragment(SportsListFragment())
                }
                R.id.tab_my_events -> {
                    switchFragment(UserEventsFragment())
                }
                R.id.tab_profile -> {
                    switchFragment(ProfileFragment())
                }
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
