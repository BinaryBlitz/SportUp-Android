package ru.binaryblitz.SportUp.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.HorizontalCalendarListener
import kotlinx.android.synthetic.main.activity_events_feed.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.adapters.EventsAdapter
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.models.Event
import ru.binaryblitz.SportUp.presenters.EventsPresenter
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SportEventsActivity : BaseActivity() {
    private lateinit var adapter: EventsAdapter

    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val EXTRA_NAME = "name"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    var typeId = 0

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_feed)
        dependencies()!!.inject(this)

        initCalendar()
        initList()
        initToolbar()
        setOnClickListeners()

        load(dateToString(Date()))
    }

    private fun initToolbar() {
        titleTextView.text = intent.getStringExtra(EXTRA_NAME)
        color = intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
        appBarView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }

        rightBtn.setOnClickListener {
            val intent = Intent(this@SportEventsActivity, EventsMapActivity::class.java)
            intent.putExtra(EXTRA_ID, typeId)
            intent.putExtra(EXTRA_COLOR, color)
            startActivity(intent)
        }
    }

    private fun initCalendar() {
        val horizontalCalendar = HorizontalCalendar.Builder(this, R.id.calendarView)
                .datesNumberOnScreen(7)
                .dayNameFormat("EEE")
                .dayNumberFormat("dd")
                .monthFormat("MMM")
                .showDayName(true)
                .showMonthName(false)
                .textColor(Color.WHITE, intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR))
                .selectedDateBackground(Color.WHITE)
                .selectorColor(Color.TRANSPARENT)
                .build()

        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Date, position: Int) {
                load(dateToString(date))
            }
        }
    }

    private fun dateToString(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(date)
    }

    private fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = EventsAdapter(this)
        recyclerView.adapter = adapter
    }

    fun onLoaded(collection: ArrayList<Event>) {
        eventsCollection = collection
        adapter.setCollection(collection)
        adapter.notifyDataSetChanged()
    }

    private fun load(date: String) {
        val presenter = EventsPresenter(api, this)
        typeId = intent.getIntExtra(EXTRA_ID, 0)
        presenter.getEvents(DeviceInfoStore.getCityObject(this)!!.id, typeId, date)
    }

    companion object {
        var eventsCollection = ArrayList<Event>()
        var color = 0
    }
}
