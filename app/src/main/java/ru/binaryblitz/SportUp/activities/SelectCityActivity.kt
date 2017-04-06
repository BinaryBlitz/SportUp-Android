package ru.binaryblitz.SportUp.activities

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.JsonObject
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.activity_select_city.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.adapters.CitiesAdapter
import ru.binaryblitz.SportUp.base.LocationDependentActivity
import ru.binaryblitz.SportUp.presenters.CitiesPresenter
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.CustomPhoneNumberTextWatcher
import javax.inject.Inject

class SelectCityActivity : LocationDependentActivity(), SwipeRefreshLayout.OnRefreshListener {
    private var adapter: CitiesAdapter? = null
    private var allCitiesList: ArrayList<CitiesAdapter.City>? = null
    private var phone: MaterialEditText? = null
    private var city: MaterialEditText? = null

    @Inject
    lateinit var api: EndpointsService

    override fun onLocationUpdated(latitude: Double?, longitude: Double?) {
        if (adapter!!.itemCount == 0 || latitude == null || longitude == null) {
            cityError()
        } else {
            adapter?.selectCity(latitude, longitude)
        }
    }

    override fun onLocationPermissionGranted() {
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

    private fun showDialog() {
        val dialog = MaterialDialog.Builder(this@SelectCityActivity)
                .title(R.string.app_name)
                .customView(R.layout.city_not_found_dialog, true)
                .positiveText(R.string.send_code)
                .negativeText(R.string.back_code)
                .autoDismiss(false)
                .onPositive({ dialog, _ ->
                    if (checkDialogInput()) {
                        sendSubscription(dialog)
                    }
                })
                .onNegative({ dialog, _ -> dialog.dismiss() })
                .show()

        initDialog(dialog)
    }

    private fun initDialog(dialog: MaterialDialog) {
        val view = dialog.customView ?: return
        phone = view.findViewById(R.id.editText) as MaterialEditText
        city = view.findViewById(R.id.editText2) as MaterialEditText
        phone?.addTextChangedListener(CustomPhoneNumberTextWatcher())
    }

    private fun generateJson(): JsonObject {
        val obj = JsonObject()
        val toSend = JsonObject()

        obj.addProperty("phone_number", phone?.text.toString())
        obj.addProperty("content", city?.text.toString())

        toSend.add("subscription", obj)

        return toSend
    }

    private fun sendSubscription(dialog: MaterialDialog) {
        // TODO
    }

    private fun checkDialogInput(): Boolean {
        var res = true
        if (!AndroidUtilities.validatePhone(phone?.text.toString())) {
            phone?.error = getString(R.string.wrong_phone_code)
            res = false
        }

        if (city?.text.toString().isEmpty()) {
            city?.error = getString(R.string.wrong_city_code)
            res = false
        }

        return res
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }

        nearBtn.setOnClickListener { checkPermissions() }

        findViewById(R.id.city_not_found_button).setOnClickListener { showDialog() }
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

        val foundItems = adapter?.getCities()!!
                .sortedWith(compareBy { it.city.name })
                .filter { AndroidUtilities.nameEqualsTo(it.city.name, query) }
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
