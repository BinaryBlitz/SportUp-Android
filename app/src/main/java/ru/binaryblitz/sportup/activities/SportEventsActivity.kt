package ru.binaryblitz.sportup.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import devs.mulham.horizontalcalendar.HorizontalCalendar
import kotlinx.android.synthetic.main.activity_events_feed.*
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.adapters.EventsAdapter
import ru.binaryblitz.sportup.base.BaseActivity
import ru.binaryblitz.sportup.models.Event
import ru.binaryblitz.sportup.presenters.EventsPresenter
import ru.binaryblitz.sportup.server.EndpointsService
import java.util.*
import javax.inject.Inject

class SportEventsActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var adapter: EventsAdapter

    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val EXTRA_NAME = "name"
    val DEFAULT_COLOR = Color.parseColor("#212121")

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

        load()
    }

    private fun initToolbar() {
        titleTextView.text = intent.getStringExtra(EXTRA_NAME)
        appBarView.setBackgroundColor(intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR))
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }
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
    }

    private fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = EventsAdapter(this)
        recyclerView.adapter = adapter

        refresh.setOnRefreshListener(this)
        refresh.setColorSchemeResources(R.color.colorAccent)
    }

    fun onLoaded(collection: ArrayList<Event>) {
        adapter.setCollection(collection)
        adapter.notifyDataSetChanged()
    }

    private fun load() {
        val presenter = EventsPresenter(api, this)
        presenter.getEvents(intent.getIntExtra(EXTRA_ID, 0), "21-04-2017")
    }

    override fun onRefresh() {
        load()
    }
}
