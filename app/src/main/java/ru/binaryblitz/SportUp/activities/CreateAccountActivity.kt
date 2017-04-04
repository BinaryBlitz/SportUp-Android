package ru.binaryblitz.SportUp.activities

import android.os.Bundle
import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.presenters.CreateUserPresenter
import ru.binaryblitz.SportUp.server.EndpointsService
import javax.inject.Inject

class CreateAccountActivity : BaseActivity() {

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_create_account)
        dependencies()!!.inject(this)
    }

    fun onLoaded() {
    }

    private fun createUser() {
        val presenter = CreateUserPresenter(api, this)
        presenter.createUser(JsonObject())
    }
}