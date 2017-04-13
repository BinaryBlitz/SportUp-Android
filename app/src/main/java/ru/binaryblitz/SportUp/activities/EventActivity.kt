package ru.binaryblitz.SportUp.activities

import android.app.ProgressDialog
import android.content.Intent
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
import javax.inject.Inject
import com.google.android.gms.maps.model.MapStyleOptions
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.utils.*
import ru.binaryblitz.SportUp.utils.LogUtil
import java.util.*

class EventActivity : BaseActivity(), OnMapReadyCallback {
    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val DEFAULT_COLOR = Color.parseColor("#212121")
    val EXTRA_USER_LIMIT = "user_limit"
    val EXTRA_USER_COUNT = "user_count"
  
    var color = 0
    var id = 0
    var userLimit = 0
    var userCount = 0
    lateinit var dialog: ProgressDialog

    var isCreatedByUser: Boolean = false
    var isJoined: Boolean = false

    var memberShipId = 0

    private var googleMap: GoogleMap? = null
    private lateinit var presenter: EventPresenter

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        dependencies()!!.inject(this)

        dialog = ProgressDialog(this)

        initToolbar()
        setOnClickListeners()
        initMap()
    }

    fun onEventJoined(id: Int) {
        memberShipId = id
        isJoined = true
        initButton()
    }

    fun onEventLeft() {
        isJoined = false
        initButton()
    }

    fun onEventDeleted() {
        finish()
    }

    private fun initToolbar() {
        color = intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
        appBarView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
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

    override fun onResume() {
        super.onResume()
        load()
        appBarView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
    }

    private fun setUpMap() {
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.uiSettings?.isMapToolbarEnabled = false

        val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.white_map)
        googleMap?.setMapStyle(style)
    }

    private fun setOnClickListeners() {
        backButton.setOnClickListener { finish() }

        playersButton.setOnClickListener { openPlayersActivity() }

        joinButton.setOnClickListener { processJoinButton() }
    }

    private fun openPlayersActivity() {
        val intent = Intent(this@EventActivity, UserListActivity::class.java)
        intent.putExtra(EXTRA_ID, id)
        intent.putExtra(EXTRA_COLOR, color)
        intent.putExtra(EXTRA_USER_LIMIT, userLimit)
        intent.putExtra(EXTRA_USER_COUNT, userCount)
        startActivity(intent)
    }

    private fun processJoinButton() {
        if (isCreatedByUser) {
            presenter.deleteEvent(id, DeviceInfoStore.getToken(this))
        } else if (isJoined) {
            presenter.leaveEvent(id, DeviceInfoStore.getToken(this))
        } else {
            presenter.joinEvent(id, DeviceInfoStore.getToken(this))
        }

        rightButton.setOnClickListener {
            if (!isCreatedByUser) {
                return@setOnClickListener
            }

            val intent = Intent(this, EditEventActivity::class.java)
            intent.putExtra(EXTRA_COLOR, color)
            startActivity(intent)
        }
    }

    fun onLoaded(obj: JsonObject) {
        eventJson = obj

        parseGeneralInfo(obj)
        parseMembersInfo(obj)

        val time = parseTime(obj)
        VotesActivity.time = time
        timeString.text = time

        parseEventStartDate(DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("starts_at"))))

        onLoaded(AndroidUtilities.getDoubleFieldFromJson(obj.get("latitude")),
                AndroidUtilities.getDoubleFieldFromJson(obj.get("longitude")))

        dialog.dismiss()
    }

    private fun parseTime(obj: JsonObject): SpannableStringBuilder {
        VotesActivity.endDate = DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("ends_at")))

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

        val price = AndroidUtilities.getIntFieldFromJson(obj.get("price"))

        if (price == 0) {
            priceText.setTextColor(color)
            priceText.text = getString(R.string.free)
        } else {
            priceText.text = price.toString() + getString(R.string.ruble_sign)
        }
    }

    private fun parseEventStartDate(date: Date) {
        val calendar = getTimeBeforeEventStarts(date)

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
        userLimit = AndroidUtilities.getIntFieldFromJson(obj.get("user_limit"))
        userCount = AndroidUtilities.getIntFieldFromJson(obj.get("user_count"))

        membersCountText.text = AndroidUtilities.getStringFieldFromJson(obj.get("user_count"))+
                " / " + AndroidUtilities.getStringFieldFromJson(obj.get("user_limit"))

        teamsText.text = "( " + obj.get("team_limit").asString + getString(R.string.teams_code)

        this.isCreatedByUser = obj.get("creator").asJsonObject.get("id").asInt == DeviceInfoStore.getUserObject(this)?.id
        this.isJoined = obj.get("membership") != null && !obj.get("membership").isJsonNull

        initButton()
    }

    private fun initMainButton() {
        initButtons()
    }

    private fun initButton() {
        try {
            joinButton.backgroundTintList = if (isJoined) ColorStateList.valueOf(ContextCompat.getColor(this, R.color.redColor))
                else ColorStateList.valueOf(color)
            joinButton.text = if (isCreatedByUser) getString(R.string.delete) else if (isJoined) getString(R.string.leave) else getString(R.string.join)
        } catch (e: Exception) {
            LogUtil.logException(e)
        }
    }

    private fun initButtons() {
        initMainButton()
        initToolbarButton()
    }

    private fun initToolbarButton() {
        rightButton.setImageResource(if (isCreatedByUser) R.drawable.ic_edit else R.drawable.icon_nav_comment_white)
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

    private fun getTimeBeforeEventStarts(date: Date): Calendar? {
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
        dialog.show()
        presenter = EventPresenter(api, this)
        id = intent.getIntExtra(EXTRA_ID, 0)
        presenter.getEvent(id, DeviceInfoStore.getToken(this))
    }

    companion object {
        lateinit var eventJson: JsonObject
        var sportTypeId: Int? = null
        var color = 0
    }
}
