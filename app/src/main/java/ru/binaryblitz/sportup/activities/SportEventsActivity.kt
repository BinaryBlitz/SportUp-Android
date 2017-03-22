package ru.binaryblitz.sportup.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import devs.mulham.horizontalcalendar.HorizontalCalendar
import kotlinx.android.synthetic.main.activity_select_city.*
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.adapters.GamesAdapter
import ru.binaryblitz.sportup.base.BaseActivity
import ru.binaryblitz.sportup.models.Game
import ru.binaryblitz.sportup.presenters.GamesPresenter
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.utils.DateUtils
import java.util.*
import javax.inject.Inject

class SportEventsActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var adapter: GamesAdapter
    val EXTRA_ID = "id"

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_feed)
        dependencies()!!.inject(this)

        initCalendar()
        initList()

        load()
    }

    private fun initCalendar() {
        val horizontalCalendar = HorizontalCalendar.Builder(this, R.id.calendarView)
                .datesNumberOnScreen(7)
                .dayNameFormat("EEE")
                .dayNumberFormat("dd")
                .monthFormat("MMM")
                .showDayName(true)
                .showMonthName(false)
                .textColor(Color.WHITE, ContextCompat.getColor(this, R.color.colorPrimary))
                .selectedDateBackground(Color.WHITE)
                .selectorColor(Color.TRANSPARENT)
                .build()
    }

    private fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = GamesAdapter(this)
        recyclerView.adapter = adapter

        refresh.setOnRefreshListener(this)
        refresh.setColorSchemeResources(R.color.colorAccent)
    }

    fun onLoaded(collection: ArrayList<Game>) {
        adapter.setCollection(collection)
        adapter.notifyDataSetChanged()
    }

    private fun load() {
        val presenter = GamesPresenter(api, this)
        presenter.getGames(intent.getIntExtra(EXTRA_ID, 0), "21-04-2017")
    }

    override fun onRefresh() {
        load()
    }
}
