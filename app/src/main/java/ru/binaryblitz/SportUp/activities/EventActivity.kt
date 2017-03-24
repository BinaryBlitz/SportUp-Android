package ru.binaryblitz.SportUp.activities

import android.graphics.Color
import android.os.Bundle
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_event.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.presenters.EventPresenter
import ru.binaryblitz.SportUp.server.EndpointsService
import javax.inject.Inject

class EventActivity : BaseActivity() {
    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        dependencies()!!.inject(this)

        setOnClickListeners()

        load()
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }
    }

    fun onLoaded(obj: JsonObject) {

    }

    private fun load() {
        val presenter = EventPresenter(api, this)
        presenter.getEvent(intent.getIntExtra(EXTRA_ID, 0), "foobar")
    }
}