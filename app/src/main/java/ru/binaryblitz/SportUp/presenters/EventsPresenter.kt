package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonArray
import ru.binaryblitz.SportUp.activities.SportEventsActivity
import ru.binaryblitz.SportUp.models.Event
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonArrayResponseListener
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.DateUtils

class EventsPresenter(private val service: EndpointsService, private val view: SportEventsActivity) {

    fun getEvents(id: Int, sportTypeId: Int, date: String) {
        service.getEvents(id, sportTypeId, date, object : JsonArrayResponseListener {
            override fun onSuccess(array: JsonArray) {
                parseAnswer(array)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    private fun parseAnswer(array: JsonArray) {
        val collection = (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .map {
                    Event(AndroidUtilities.getIntFieldFromJson(it.get("id")),
                            AndroidUtilities.getStringFieldFromJson(it.get("name")),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("starts_at"))),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("starts_at"))),
                            AndroidUtilities.getStringFieldFromJson(it.get("address")),
                            AndroidUtilities.getIntFieldFromJson(it.get("user_limit")),
                            AndroidUtilities.getIntFieldFromJson(it.get("team_limit")),
                            AndroidUtilities.getBooleanFieldFromJson(it.get("public")),
                            AndroidUtilities.getIntFieldFromJson(it.get("price")),
                            AndroidUtilities.getDoubleFieldFromJson(it.get("latitude")),
                            AndroidUtilities.getDoubleFieldFromJson(it.get("longitude")))
                }

        view.onLoaded(collection = collection as ArrayList<Event>)
    }
}
