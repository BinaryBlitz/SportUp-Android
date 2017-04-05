package ru.binaryblitz.SportUp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_profile.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseFragment
import ru.binaryblitz.SportUp.models.User
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.utils.Image

class ProfileFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        initUser()
    }

    private fun initUser() {
        val user = DeviceInfoStore.getUserObject(context)
        Image.loadAvatar(context, user, user?.avatarUrl, profileImage)
        nameText.text = user?.firstName + " " + user?.lastName

        eventsCount.text = user?.eventsCount.toString()
        votesCount.text = user?.votesCount.toString()
        violationsCount.text = user?.violationsCount.toString()
    }
}
