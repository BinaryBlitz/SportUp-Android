package ru.binaryblitz.SportUp.activities

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
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
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.binaryblitz.SportUp.utils.LogUtil
import java.util.*

class EventActivity : BaseActivity(), OnMapReadyCallback {
    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    var color = 0

    private var googleMap: GoogleMap? = null

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        dependencies()!!.inject(this)

        initToolbar()
        setOnClickListeners()
        initMap()
    }

    private fun initToolbar() {
        color = intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
        appBarView.setBackgroundColor(color)
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
        parseMembersInfo(obj)

        timeString.text = parseTime(obj)

        parseEventStartDate(DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("starts_at"))))

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

    private fun parseEventStartDate(date: Date) {
        val calendar = getTimeBeforEventStarts(date)

        if (calendar == null) {
            timeUntilEventStarts.text = getString(R.string.completed)
            return
        }

        if (calendar.get(Calendar.DAY_OF_YEAR) > 0) {
            val daysText = resources.getQuantityString(R.plurals.days, calendar.get(Calendar.DAY_OF_YEAR), calendar.get(Calendar.DAY_OF_YEAR))
            timeUntilEventStarts.text = getString(R.string.before_start) + daysText
            return
        }

        timeUntilEventStarts.text = getString(R.string.before_start) + calendar.get(Calendar.HOUR_OF_DAY) +
                getString(R.string.hours_code) + calendar.get(Calendar.MINUTE) + getString(R.string.minute_code)
    }

    private fun parseMembersInfo(obj: JsonObject) {
        membersCountText.text = obj.get("user_count").asString + " / " + obj.get("user_limit").asString
        teamsText.text = "( " + obj.get("team_limit").asString + getString(R.string.teams_code)

        initButton(obj.get("membership") != null && !obj.get("membership").isJsonNull)
    }

    private fun initButton(isJoined: Boolean) {
        try {
            joinBtn.backgroundTintList = if (isJoined) ColorStateList.valueOf(ContextCompat.getColor(this, R.color.redColor))
                else ColorStateList.valueOf(color)
            joinBtn.text = if (isJoined) getString(R.string.leave) else getString(R.string.join)
        } catch (e: Exception) {
            LogUtil.logException(e)
        }
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

    private fun getTimeBeforEventStarts(date: Date): Calendar? {
        val millisUntilEventStarts = date.time - System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        if (millisUntilEventStarts < 0) {
            return null
        }

        calendar.time = DateTime(millisUntilEventStarts, DateTimeZone.getDefault()).toDate()
        return calendar
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
        presenter.getEvent(intent.getIntExtra(EXTRA_ID, 0), "foobar")
    }
}
