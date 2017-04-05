package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.EventActivity
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonObjectResponseListener
import ru.binaryblitz.SportUp.utils.LogUtil

class EventPresenter(private val service: EndpointsService, private val view: EventActivity) {

    fun getEvent(id: Int, token: String) {
        service.getEvent(id, token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseAnswer(obj)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    fun joinEvent(id: Int, token: String) {
        service.joinEvent(id, token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                onEventJoined(obj)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    fun leaveEvent(id: Int, token: String) {
        service.leaveEvent(id, token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                onEventLeft()
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    fun deleteEvent(id: Int, token: String) {
        service.deleteEvent(id, token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                onEventDeleted()
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    private fun onEventJoined(obj: JsonObject) {
        view.onEventJoined(obj.get("id").asInt)
    }

    private fun onEventLeft() {
        view.onEventLeft()
    }

    private fun onEventDeleted() {
        view.onEventDeleted()
    }

    private fun parseAnswer(obj: JsonObject) {
        view.onLoaded(obj)
    }
}