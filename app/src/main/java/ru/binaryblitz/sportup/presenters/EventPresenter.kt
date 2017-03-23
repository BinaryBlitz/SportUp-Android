package ru.binaryblitz.sportup.presenters

import com.google.gson.JsonObject
import ru.binaryblitz.sportup.activities.EventsMapActivity
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.server.JsonObjectResponseListener
import ru.binaryblitz.sportup.utils.LogUtil

class EventPresenter(private val service: EndpointsService, private val view: EventsMapActivity) {

    fun getEvent(id: Int) {
        LogUtil.logError(id)
        service.getEvent(id, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseAnswer(obj)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
                LogUtil.logError(networkError)
            }
        })
    }

    private fun parseAnswer(obj: JsonObject) {
        LogUtil.logError(obj.toString())
        view.onEventLoaded(obj)
    }
}