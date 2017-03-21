package ru.binaryblitz.sportup.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonArray
import com.miguelcatalan.materialsearchview.MaterialSearchView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.adapters.CitiesAdapter
import ru.binaryblitz.sportup.base.BaseActivity
import ru.binaryblitz.sportup.custom.RecyclerListView
import ru.binaryblitz.sportup.models.City
import ru.binaryblitz.sportup.server.ServerApi
import ru.binaryblitz.sportup.utils.AndroidUtilities
import ru.binaryblitz.sportup.utils.LocationManager

class SelectCityActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var adapter: CitiesAdapter? = null
    private var layout: SwipeRefreshLayout? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var searchView: MaterialSearchView? = null
    private var allCitiesList: ArrayList<CitiesAdapter.City>? = null

    val locationManager = LocationManager(this@SelectCityActivity, mGoogleApiClient,
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

        initList()
        initToolbar()
        initGoogleApiClient()
        setOnClickListeners()
        initSearchView()

        Handler().post {
            layout!!.isEnabled = true
            load()
        }
    }

    private fun initGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu.findItem(R.id.action_search)
        searchView?.setMenuItem(item)

        return true
    }

    private fun initSearchView() {
        searchView = findViewById(R.id.search_view) as MaterialSearchView
        searchView?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
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

        searchView?.setHint(getString(R.string.items_search))
        searchView?.setVoiceSearch(false)
    }

    private fun nameEqualsTo(item: City, query: String): Boolean {
        if (item.name == null) {
            return false
        }

        return item.name!!.toLowerCase().contains(query)
    }

    private fun searchForItems(query: String) {
        if (adapter?.getCities() == null) {
            return
        }

        val foundItems = adapter?.getCities()!!.filter { nameEqualsTo(it.city, query) }
        adapter?.setCollection(foundItems)
        adapter?.notifyDataSetChanged()
    }

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        title = null
    }

    override fun onBackPressed() {
        if (searchView == null) {
            return
        }
        if (searchView!!.isSearchOpen) {
            searchView?.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRefresh() {
        load()
    }

    override fun onConnected(bundle: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
                } else {
                    locationManager.getLocation()
                }
            } catch (ignored: Exception) {
                layout?.isRefreshing = false
            }

        } else {
            locationManager.getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.getLocation()
                } else {
                    onLocationError()
                }
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {
        mGoogleApiClient?.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        layout?.isRefreshing = false
        onLocationError()
    }

    fun cityError() {
        Snackbar.make(findViewById(R.id.main), R.string.city_error, Snackbar.LENGTH_SHORT).show()
    }

    private fun setOnClickListeners() {
        findViewById(R.id.back_btn).setOnClickListener { finish() }

        findViewById(R.id.near_btn).setOnClickListener {
            locationManager.getLocation()
        }
    }

    private fun initList() {
        val view = findViewById(R.id.recyclerView) as RecyclerListView
        view.layoutManager = LinearLayoutManager(this)
        view.itemAnimator = DefaultItemAnimator()
        view.setHasFixedSize(true)

        adapter = CitiesAdapter(this)
        view.adapter = adapter

        layout = findViewById(R.id.refresh) as SwipeRefreshLayout
        layout?.setOnRefreshListener(this)
        layout?.setColorSchemeResources(R.color.colorAccent)
    }

    private fun load() {
        ServerApi.get(this).api().citiesList.enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                layout?.isRefreshing = false
                if (response.isSuccessful) {
                    parseAnswer(response.body())
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                layout?.isRefreshing = false
                onInternetConnectionError()
            }
        })
    }

    private fun parseAnswer(array: JsonArray) {
        val collection = (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .map {
                    City(it.get("id").asInt,
                            AndroidUtilities.getStringFieldFromJson(it.get("name")),
                            AndroidUtilities.getDoubleFieldFromJson(it.get("latitude")),
                            AndroidUtilities.getDoubleFieldFromJson(it.get("longitude")))
                }
                .map { CitiesAdapter.City(it, false) }

        allCitiesList = collection as ArrayList<CitiesAdapter.City>

        adapter?.setCollection(collection)
        adapter?.notifyDataSetChanged()
    }

    companion object {
        private val LOCATION_PERMISSION = 2
    }
}
