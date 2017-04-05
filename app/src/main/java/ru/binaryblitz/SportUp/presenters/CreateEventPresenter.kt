package ru.binaryblitz.SportUp.presenters

import android.graphics.Color
import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.CreateEventActivity
import ru.binaryblitz.SportUp.activities.EventActivity
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonObjectResponseListener
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.LogUtil

class CreateEventPresenter(private val service: EndpointsService, private val view: CreateEventActivity) {

    fun createEvent(event: JsonObject, token: String) {
        service.sendEvent(event, token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseAnswer(obj)
            }

            override fun onError(networkError: String) {
                LogUtil.logError(networkError)
                view.onInternetConnectionError()
            }
        })
    }

    private fun parseAnswer(obj: JsonObject) {
        view.onLoaded(AndroidUtilities.getIntFieldFromJson(obj.get("id")),
                Color.parseColor(AndroidUtilities.getStringFieldFromJson(obj.get("event").asJsonObject.get("sport_type").asJsonObject.get("color"))))
    }
}