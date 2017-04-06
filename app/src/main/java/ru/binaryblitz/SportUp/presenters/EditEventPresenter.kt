package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.EditEventActivity
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonObjectResponseListener
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.LogUtil

class EditEventPresenter(private val service: EndpointsService, private val view: EditEventActivity) {

    fun editEvent(id: Int, event: JsonObject, token: String) {
        service.editEvent(event, id, token, object : JsonObjectResponseListener {
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
        view.onLoaded(AndroidUtilities.getIntFieldFromJson(obj.get("id")))
    }
}