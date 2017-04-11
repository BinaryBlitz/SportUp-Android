package ru.binaryblitz.SportUp.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_players_list.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.adapters.PlayersAdapter
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.presenters.PlayersPresenter
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import javax.inject.Inject

class UserListActivity : BaseActivity() {
    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val EXTRA_USER_LIMIT = "user_limit"
    val EXTRA_USER_COUNT = "user_count"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    var id = 0
    var isJoinedTeam = false

    lateinit var presenter: PlayersPresenter

    private lateinit var adapter: PlayersAdapter

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_list)
        dependencies()!!.inject(this)

        initToolbar()
        initList()
        setOnClickListeners()
        load()
    }

    fun joinTeam(number: Int) {
        if (isJoinedTeam) {
            presenter.updateTeam(id, generateJson(number), DeviceInfoStore.getToken(this))
        } else {
            presenter.joinTeam(id, generateJson(number), DeviceInfoStore.getToken(this))
        }
    }

    private fun generateJson(number: Int): JsonObject {
        val obj = JsonObject()

        obj.addProperty("team_number", number)

        return obj
    }

    fun onTeamJoined() {
        adapter.notifyDataSetChanged()
    }

    fun onTeamUpdate() {
        adapter.notifyDataSetChanged()
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

        adapter = PlayersAdapter(this)
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }
    }

    fun onLoaded(collection: ArrayList<Pair<String, Any>>) {
        adapter.setCollection(collection)
        adapter.notifyDataSetChanged()
    }

    private fun load() {
        presenter = PlayersPresenter(api, this)
        val userLimit = intent.getIntExtra(EXTRA_USER_LIMIT, 0)
        val userCount = intent.getIntExtra(EXTRA_USER_COUNT, 0)
        idText.text = "$userCount / $userLimit"

        id = intent.getIntExtra(EXTRA_ID, 0)
        presenter.getTeams(id, DeviceInfoStore.getToken(this), userLimit)
    }
}
