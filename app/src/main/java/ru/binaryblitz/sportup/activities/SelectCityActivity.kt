package ru.binaryblitz.sportup.activities

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_select_city.*
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.adapters.CitiesAdapter
import ru.binaryblitz.sportup.base.LocationDependentActivity
import ru.binaryblitz.sportup.presenters.CitiesPresenter
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.utils.AndroidUtilities
import javax.inject.Inject

class SelectCityActivity : LocationDependentActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var adapter: CitiesAdapter? = null
    private var allCitiesList: ArrayList<CitiesAdapter.City>? = null

    @Inject
    lateinit var api: EndpointsService

    override fun onLocationUpdated(latitude: Double?, longitude: Double?) {
        if (adapter!!.itemCount == 0 || latitude == null || longitude == null) {
            cityError()
        } else {
            adapter?.selectCity(latitude, longitude)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)
        dependencies()!!.inject(this)

        initList()
        initToolbar()
        initGoogleApiClient()
        initSearchView()
        setOnClickListeners()

        Handler().post {
            refresh.isEnabled = true
            load()
        }
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }

        nearBtn.setOnClickListener { checkPermissions() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)

        return true
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    adapter?.setCollection(allCitiesList!!)
                    adapter?.notifyDataSetChanged()
                    return false
                }

                searchForItems(newText)
                return false
            }
        })

        searchView.setHint(getString(R.string.items_search))
        searchView.setVoiceSearch(false)
    }

    private fun searchForItems(query: String) {
        if (adapter?.getCities() == null) {
            return
        }

        val foundItems = adapter?.getCities()!!.filter { AndroidUtilities.nameEqualsTo(it.city.name, query) }
        adapter?.setCollection(foundItems)
        adapter?.notifyDataSetChanged()
    }

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        title = null
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRefresh() {
        load()
    }

    fun cityError() {
        Snackbar.make(main, R.string.city_error, Snackbar.LENGTH_SHORT).show()
    }

    private fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = CitiesAdapter(this)
        recyclerView.adapter = adapter

        refresh.setOnRefreshListener(this)
        refresh.setColorSchemeResources(R.color.colorAccent)
    }

    fun showLoadingIndicator() {
        refresh.isRefreshing = true
    }

    fun hideLoadingIndicator() {
        refresh.isRefreshing = false
    }

    fun onLoaded(collection: ArrayList<CitiesAdapter.City>) {
        allCitiesList = collection

        adapter?.setCollection(collection)
        adapter?.notifyDataSetChanged()
    }

    private fun load() {
        val presenter = CitiesPresenter(api, this)
        presenter.getCityList()
    }
}
