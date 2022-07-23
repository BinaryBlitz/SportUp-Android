package ru.binaryblitz.SportUp.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.fragments.ProfileFragment
import ru.binaryblitz.SportUp.fragments.SportsListFragment
import ru.binaryblitz.SportUp.fragments.UserEventsFragment
import ru.binaryblitz.SportUp.presenters.UserPresenter
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AppConfig
import javax.inject.Inject

class MainActivity : BaseActivity() {
    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dependencies()!!.inject(this)

        bottomBar.setOnTabSelectListener { tabId ->
            when (tabId) {
                R.id.tab_sports -> {
                    switchFragment(SportsListFragment())
                }
                R.id.tab_my_events -> {
                    if (AppConfig.checkIfUserLoggedIn(this)) {
                        switchFragment(UserEventsFragment())
                    }
                }
                R.id.tab_profile -> {
                    switchFragment(ProfileFragment())
                }
            }
        }

        getUser()
    }

    private fun getUser() {
        val presenter = UserPresenter(api, this)
        presenter.getUser(DeviceInfoStore.getToken(this))
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
