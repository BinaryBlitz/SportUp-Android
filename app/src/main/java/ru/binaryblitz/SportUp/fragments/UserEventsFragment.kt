package ru.binaryblitz.SportUp.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_user_events.*
import kotlinx.android.synthetic.main.dialog_password.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.activities.EventActivity
import ru.binaryblitz.SportUp.activities.SportEventsActivity
import ru.binaryblitz.SportUp.adapters.MyEventsAdapter
import ru.binaryblitz.SportUp.base.BaseFragment
import ru.binaryblitz.SportUp.presenters.MyEventsPresenter
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.Animations
import javax.inject.Inject

class UserEventsFragment : BaseFragment() {
    private lateinit var adapter: MyEventsAdapter
    private var dialogOpened = false

    val EXTRA_ID = "id"
    val EXTRA_COLOR = "color"

    @Inject
    lateinit var api: EndpointsService

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_user_events, container, false)
    }

    override fun onStart() {
        super.onStart()
        dependencies()!!.inject(this)

        initList()
        load()
    }

    fun showPasswordDialog(password: String, eventId: Int) {
        Handler().post {
            dialogOpened = true
            Animations.animateRevealShow(view?.findViewById(R.id.dialog_new_order), activity)
            passwordButton.setOnClickListener {
                if (password == passwordEdit.text.toString()) {
                    openEvent(eventId)
                } else {
                    passwordEdit.setError(getString(R.string.wrong_password), null)
                }
            }
        }
    }

    private fun openEvent(eventId: Int) {
        val intent = Intent(context, EventActivity::class.java)
        intent.putExtra(EXTRA_ID, eventId)
        intent.putExtra(EXTRA_COLOR, SportEventsActivity.color)
        context.startActivity(intent)
    }

    fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        adapter = MyEventsAdapter(this)
        recyclerView.adapter = adapter
    }

    fun onLoaded(collection: ArrayList<Pair<String, Any>>) {
        adapter.setCollection(collection)
        adapter.notifyDataSetChanged()
    }

    private fun load() {
        val presenter = MyEventsPresenter(api, this)
        presenter.getEvents("foobar")
    }
}
