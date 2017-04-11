package ru.binaryblitz.SportUp.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_votes.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.adapters.VotesAdapter
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.models.Player
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import javax.inject.Inject

class VotesActivity : BaseActivity() {
    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    private lateinit var adapter: VotesAdapter

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_votes)

        initToolbar()
        initList()
        setOnClickListeners()

        showPlayers()
    }

    private fun initToolbar() {
        val color = intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
        appBarView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
    }

    fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = VotesAdapter(this)
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }
    }

    fun showPlayers() {
        adapter.setCollection(players)
        adapter.notifyDataSetChanged()
    }

    companion object {
        var players: ArrayList<Player> = ArrayList()
    }
}

