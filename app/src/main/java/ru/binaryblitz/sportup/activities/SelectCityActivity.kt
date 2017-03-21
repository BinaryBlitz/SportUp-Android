package ru.binaryblitz.sportup.activities

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_select_city.*
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.adapters.CitiesAdapter
import ru.binaryblitz.sportup.base.BaseActivity
import ru.binaryblitz.sportup.presenters.CitiesPresenter
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.utils.AndroidUtilities
import ru.binaryblitz.sportup.utils.LocationManager
import javax.inject.Inject

class SelectCityActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var adapter: CitiesAdapter? = null
    private var allCitiesList: ArrayList<CitiesAdapter.City>? = null
    private var googleApiClient: GoogleApiClient? = null

    @Inject
    lateinit var api: EndpointsService

    val locationManager = LocationManager(this@SelectCityActivity,
            object : LocationManager.LocationUpdateListener {
                override fun onLocationUpdated(latitude: Double?, longitude: Double?) {
                    this@SelectCityActivity.onLocationUpdated(latitude, longitude)
                }
            })

    private fun onLocationUpdated(latitude: Double?, longitude: Double?) {
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

    fun getGoogleApiClient(): GoogleApiClient? {
        return googleApiClient
    }

    fun initGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)

        return true
    }

    private fun checkPermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        locationManager.getLocation()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        onLocationError()
                    }
                })
                .check()
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

    override fun onConnected(bundle: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions()
        } else {
            locationManager.getLocation()
        }
    }

    override fun onConnectionSuspended(i: Int) {
        googleApiClient?.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        refresh.isRefreshing = false
        onLocationError()
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
