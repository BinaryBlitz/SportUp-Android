package ru.binaryblitz.SportUp.activities

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import kotlinx.android.synthetic.main.activity_players_list.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.adapters.PlayersAdapter
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.presenters.SportTypesPresenter
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import javax.inject.Inject

class UserListActivity : BaseActivity() {
    private lateinit var adapter: PlayersAdapter

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_list)

        initList()
        setOnClickListeners()
        load()
    }

    fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = PlayersAdapter(this)
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }
    }

    fun onLoaded(collection: ArrayList<Pair<String, Any>>) {
        adapter.setCollection(collection)
        adapter.notifyDataSetChanged()
    }

    private fun load() {
        val presenter = SportTypesPresenter(api, this)
        presenter.getSportTypes(DeviceInfoStore.getCityObject(context)!!.id)
    }
}
