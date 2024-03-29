package ru.binaryblitz.SportUp.activities

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_votes.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.adapters.VotesAdapter
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.models.Player
import ru.binaryblitz.SportUp.presenters.VotePresenter
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import java.util.*
import javax.inject.Inject

class VotesActivity : BaseActivity() {
    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    private lateinit var adapter: VotesAdapter

    lateinit var dialog: ProgressDialog

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_votes)
        dependencies()!!.inject(this)

        initToolbar()
        initList()
        setOnClickListeners()
        initElements()
    }

    private fun initElements() {
        dialog = ProgressDialog(this)
        showPlayers()
        timeString.text = time
        setVoteEndTimeText()
    }

    private fun setVoteEndTimeText() {
        val calendar = getTimeBeforeEventStarts()
        calendar?.add(Calendar.HOUR, 24)

        timeUntilVoteEnds.text = String.format(resources.getString(R.string.time_until_voting_ends), calendar?.get(Calendar.HOUR),
                calendar?.get(Calendar.MINUTE))
    }

    private fun getTimeBeforeEventStarts(): Calendar? {
        if (endDate == null) {
            return null
        }

        val millisUntilVotingEnds = endDate!!.time - System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        calendar.time = DateTime(millisUntilVotingEnds, DateTimeZone.getDefault()).toDate()
        return calendar
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

    fun onLoaded(obj: JsonObject) {
        dialog.dismiss()
        finish()
    }

    private fun generateJson(id: Int): JsonObject {
        val vote = JsonObject()
        vote.addProperty("voted_user_id", id)

        return vote
    }

    fun vote(id: Int) {
        dialog.show()
        val presenter = VotePresenter(api, this)
        presenter.vote(generateJson(id), intent.getIntExtra(EXTRA_ID, 0), DeviceInfoStore.getToken(this))
    }

    companion object {
        var players: ArrayList<Player> = ArrayList()
        var time: SpannableStringBuilder? = null
        var endDate: Date? = null
    }
}

