package ru.binaryblitz.SportUp.presenters

import android.util.Log
import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.CreateAccountActivity
import ru.binaryblitz.SportUp.activities.EventActivity
import ru.binaryblitz.SportUp.server.DeviceInfoStore
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
                view.dismissProgress()
                LogUtil.logError(networkError)
            }
        })
    }

    private fun parseAnswer(obj: JsonObject) {
        if (obj.get("api_token") == null || obj.get("api_token").isJsonNull || obj.get("api_token").asString.isEmpty()) {
            view.onLoaded(false)
        }

        DeviceInfoStore.saveToken(view, obj.get("api_token").asString)

        try {
            val user = DeviceInfoStore.getUserObject(view)
            user?.id = obj.get("id").asInt
            DeviceInfoStore.saveUser(view, user!!)
        } catch (e: Exception) {
            view.onLoaded(false)
        }

        view.onLoaded(true)
    }
}