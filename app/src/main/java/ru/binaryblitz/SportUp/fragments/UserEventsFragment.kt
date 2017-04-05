package ru.binaryblitz.SportUp.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_user_events.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.activities.CreateEventActivity
import ru.binaryblitz.SportUp.adapters.MyEventsAdapter
import ru.binaryblitz.SportUp.base.BaseFragment
import ru.binaryblitz.SportUp.presenters.MyEventsPresenter
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AppConfig
import javax.inject.Inject

class UserEventsFragment : BaseFragment() {
    private lateinit var adapter: MyEventsAdapter

    @Inject
    lateinit var api: EndpointsService

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_user_events, container, false)
    }

    override fun onStart() {
        super.onStart()
        dependencies()!!.inject(this)

        initList()
        load()

        rightBtn.setOnClickListener {
            if (!AppConfig.checkIfUserLoggedIn(context)) {
                return@setOnClickListener
            }
            val intent = Intent(activity, CreateEventActivity::class.java)
            startActivity(intent)
        }
    }

    fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = MyEventsAdapter(activity)
        recyclerView.adapter = adapter
    }

    fun onLoaded(collection: ArrayList<Pair<String, Any>>) {
        adapter.setCollection(collection)
        adapter.notifyDataSetChanged()
    }

    private fun load() {
        val presenter = MyEventsPresenter(api, this)
        presenter.getEvents(DeviceInfoStore.getToken(activity))
    }
}
