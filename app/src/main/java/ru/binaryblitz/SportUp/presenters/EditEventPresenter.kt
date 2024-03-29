package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.EditEventActivity
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonObjectResponseListener

class EditEventPresenter(private val service: EndpointsService, private val view: EditEventActivity) {

    fun editEvent(id: Int, event: JsonObject, token: String) {
        service.editEvent(event, id, token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseAnswer()
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
                onEventDeleted()
            }
        })
    }

    private fun onEventDeleted() {
        view.onEventDeleted()
    }

    private fun parseAnswer() {
        view.onLoaded()
    }
}
