package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.CreateAccountActivity
import ru.binaryblitz.SportUp.activities.EventActivity
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonObjectResponseListener
import ru.binaryblitz.SportUp.utils.LogUtil

class CreateUserPresenter(private val service: EndpointsService, private val view: CreateAccountActivity) {

    fun createUser(body: JsonObject) {
        service.createUser(body, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseAnswer(obj)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    private fun parseAnswer(obj: JsonObject) {
        view.onLoaded()
    }
}