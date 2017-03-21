package ru.binaryblitz.sportup.fragments

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_select_city.*
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.adapters.SportTypesAdapter
import ru.binaryblitz.sportup.base.BaseFragment
import ru.binaryblitz.sportup.models.SportType
import ru.binaryblitz.sportup.presenters.SportTypesPresenter
import ru.binaryblitz.sportup.server.EndpointsService
import javax.inject.Inject

class SportsListFragment : BaseFragment() {
    private var adapter: SportTypesAdapter? = null

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

        adapter = SportTypesAdapter(activity)
        recyclerView.adapter = adapter

        refresh.setColorSchemeResources(R.color.colorAccent)
    }

    fun onLoaded(collection: ArrayList<SportType>) {
        adapter?.setCollection(collection)
        adapter?.notifyDataSetChanged()
    }

    private fun load() {
        val presenter = SportTypesPresenter(api, this)
        presenter.getSportTypes()
    }
}
