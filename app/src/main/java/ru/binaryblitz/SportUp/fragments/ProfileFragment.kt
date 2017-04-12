package ru.binaryblitz.SportUp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_profile.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.activities.CreateAccountActivity
import ru.binaryblitz.SportUp.activities.PromocodeActivity
import ru.binaryblitz.SportUp.base.BaseFragment
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.utils.Image

class ProfileFragment : BaseFragment() {
    val EXTRA_EDIT = "edit"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        initUser()

        rightBtn.setOnClickListener {
            val intent = Intent(context, CreateAccountActivity::class.java)
            intent.putExtra(EXTRA_EDIT, true)
            startActivity(intent)
        }

        promoButton.setOnClickListener {
            val intent = Intent(context, PromocodeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initUser() {
        val user = DeviceInfoStore.getUserObject(context)
        Image.loadAvatar(context, user?.firstName, user?.avatarUrl, profileImage)
        nameText.text = user?.firstName + " " + user?.lastName

        eventsCount.text = user?.eventsCount.toString()
        votesCount.text = user?.votesCount.toString()
        violationsCount.text = user?.violationsCount.toString()
    }
}
