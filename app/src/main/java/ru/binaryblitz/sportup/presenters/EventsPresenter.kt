package ru.binaryblitz.sportup.presenters

import com.google.gson.JsonArray
import ru.binaryblitz.sportup.activities.SportEventsActivity
import ru.binaryblitz.sportup.models.Event
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.server.JsonArrayResponseListener
import ru.binaryblitz.sportup.utils.AndroidUtilities
import ru.binaryblitz.sportup.utils.DateUtils
import ru.binaryblitz.sportup.utils.LogUtil

class EventsPresenter(private val service: EndpointsService, private val view: SportEventsActivity) {

    fun getGames(id: Int, date: String) {
        service.getGames(id, date, object : JsonArrayResponseListener {
            override fun onSuccess(array: JsonArray) {
                parseAnswer(array)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    private fun parseAnswer(array: JsonArray) {
        LogUtil.logError(array.toString())
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
                            AndroidUtilities.getIntFieldFromJson(it.get("price")))
                }

        view.onLoaded(collection = collection as ArrayList<Event>)
    }

}
