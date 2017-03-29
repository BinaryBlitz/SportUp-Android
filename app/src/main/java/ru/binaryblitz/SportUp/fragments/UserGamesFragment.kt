package ru.binaryblitz.SportUp.fragments

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_select_city.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.adapters.MyEventsAdapter
import ru.binaryblitz.SportUp.base.BaseFragment
import ru.binaryblitz.SportUp.models.SportType
import ru.binaryblitz.SportUp.presenters.SportTypesPresenter
import ru.binaryblitz.SportUp.server.EndpointsService
import javax.inject.Inject

class UserGamesFragment : BaseFragment() {
    private lateinit var adapter: MyEventsAdapter

    @Inject
    lateinit var api: EndpointsService

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_sports_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        dependencies()!!.inject(this)

        initList()
        load()
    }

    fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = MyEventsAdapter(activity)
        recyclerView.adapter = adapter

        refresh.setColorSchemeResources(R.color.colorAccent)
    }

    fun onLoaded(collection: ArrayList<Pair<String, Any>>) {
        adapter.setCollection(collection)
        adapter.notifyDataSetChanged()
    }

    private fun load() {
        val presenter = SportTypesPresenter(api, this)
        presenter.getSportTypes()
    }
}
