package ru.binaryblitz.sportup.presenters

import com.google.gson.JsonArray
import ru.binaryblitz.sportup.activities.EventsMapActivity
import ru.binaryblitz.sportup.models.MapEvent
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.server.JsonArrayResponseListener
import ru.binaryblitz.sportup.utils.AndroidUtilities

class EventMapPresenter(private val service: EndpointsService, private val view: EventsMapActivity) {

    fun getMapEvents(id: Int) {
        service.getMapEvents(id, object : JsonArrayResponseListener {
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
                    MapEvent(AndroidUtilities.getIntFieldFromJson(it.get("id")),
                            AndroidUtilities.getDoubleFieldFromJson(it.get("latitude")),
                            AndroidUtilities.getDoubleFieldFromJson(it.get("longitude")))
                }

        view.onLoaded(collection = collection as ArrayList<MapEvent>)
    }
}
