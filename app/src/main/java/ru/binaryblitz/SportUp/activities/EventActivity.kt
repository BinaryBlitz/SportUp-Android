package ru.binaryblitz.SportUp.activities

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_event.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.presenters.EventPresenter
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.DateUtils
import ru.binaryblitz.SportUp.utils.TimeSpan
import javax.inject.Inject
import com.google.android.gms.maps.model.MapStyleOptions

class EventActivity : BaseActivity(), OnMapReadyCallback {
    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    private var googleMap: GoogleMap? = null

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        dependencies()!!.inject(this)

        setOnClickListeners()
        initMap()
    }

    private fun initMap() {
        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        Handler().post { map.getMapAsync(this@EventActivity) }
    }

    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
        setUpMap()
        load()
    }

    private fun setUpMap() {
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.uiSettings?.isMapToolbarEnabled = false

        val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.white_map)
        googleMap?.setMapStyle(style)
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }
    }

    fun onLoaded(obj: JsonObject) {
        parseGeneralInfo(obj)

        timeString.text = parseTime(obj)

        onLoaded(AndroidUtilities.getDoubleFieldFromJson(obj.get("latitude")),
                AndroidUtilities.getDoubleFieldFromJson(obj.get("longitude")))
    }

    private fun parseTime(obj: JsonObject): SpannableStringBuilder {
        return getTimeString(
                DateUtils.getTimeStringRepresentation(
                        DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("starts_at")))
                ) + " - " +
                DateUtils.getTimeStringRepresentation(
                        DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("ends_at")))
                )
        )
    }

    private fun parseGeneralInfo(obj: JsonObject) {
        titleTextView.text = AndroidUtilities.getStringFieldFromJson(obj.get("name"))
        idText.text = "#" + AndroidUtilities.getStringFieldFromJson(obj.get("id"))
        descriptionText.text = AndroidUtilities.getStringFieldFromJson(obj.get("description"))
        locationText.text = AndroidUtilities.getStringFieldFromJson(obj.get("address"))
        priceText.text = AndroidUtilities.getStringFieldFromJson(obj.get("price")) + getString(R.string.ruble_sign)
    }

    fun getTimeString(date: String): SpannableStringBuilder {
        val result = date.replace(":", "")

        val builder = SpannableStringBuilder(result)
        builder.setSpan(TimeSpan(), 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(RelativeSizeSpan(0.5f), 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(TimeSpan(), 9, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(RelativeSizeSpan(0.5f), 9, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return builder
    }

    private fun moveCamera(latitude: Double?, longitude: Double?) {
        val cameraPosition = CameraPosition.Builder()
                .target(LatLng(latitude!!, longitude!!))
                .zoom(17f)
                .bearing(0f)
                .tilt(0f)
                .build()

        googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun onLoaded(latitude: Double, longitude: Double) {
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pins_footballmid)

        googleMap?.addMarker(MarkerOptions()
                .position(LatLng(latitude, longitude))
                .icon(icon))

        moveCamera(latitude, longitude)
    }

    private fun load() {
        val presenter = EventPresenter(api, this)
        presenter.getEvent(intent.getIntExtra(EXTRA_ID, 0), "foo