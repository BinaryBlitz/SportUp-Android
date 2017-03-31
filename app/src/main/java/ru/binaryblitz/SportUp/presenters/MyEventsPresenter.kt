package ru.binaryblitz.SportUp.presenters

import android.graphics.Color
import android.util.Pair
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.adapters.MyEventsAdapter
import ru.binaryblitz.SportUp.fragments.UserEventsFragment
import ru.binaryblitz.SportUp.models.MyEvent
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonArrayResponseListener
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.DateUtils
import java.util.*

class MyEventsPresenter(private val service: EndpointsService, private val view: UserEventsFragment) {

    fun getEvents(token: String) {
        service.getMyEvents(token, object : JsonArrayResponseListener {
            override fun onSuccess(array: JsonArray) {
                getInvites(token, array)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    private fun getInvites(token: String, events: JsonArray) {
        service.getInvites(token, object : JsonArrayResponseListener {
            override fun onSuccess(array: JsonArray) {
                parseAnswer(array, events)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    private fun parseAnswer(array: JsonArray, events: JsonArray) {
        val collection: ArrayList<Pair<String, Any>> = ArrayList()

        parseInvites(array, collection)
        parseCreatedEvents(events, collection)
        parseUpcomingEvents(events, collection)
        parseClosedEvents(events, collection)

        view.onLoaded(collection = collection)
    }

    private fun getEventFromJson(obj: JsonObject): MyEvent {
        return MyEvent(
                AndroidUtilities.getIntFieldFromJson(obj.get("id")),
                AndroidUtilities.getStringFieldFromJson(obj.get("event").asJsonObject.get("name")),
                DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("event").asJsonObject.get("starts_at"))),
                DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("event").asJsonObject.get("ends_at"))),
                AndroidUtilities.getStringFieldFromJson(obj.get("event").asJsonObject.get("sport_type").asJsonObject.get("icon_url")),
                Color.parseColor(AndroidUtilities.getStringFieldFromJson(obj.get("event").asJsonObject.get("sport_type").asJsonObject.get("color")))
        )
    }

    private fun parseInvites(array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        if (array.size() != 0) {
            collection.add(Pair(MyEventsAdapter.HEADER_CODE, view.context.getString(R.string.invite_code)))

            (0..array.size())
                    .map { array.get(it).asJsonObject }
                    .mapTo(collection) { Pair(MyEventsAdapter.INVITE_CODE, getEventFromJson(it)) }
        }
    }

    private fun parseCreatedEvents(array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        val startIndex = collection.size

        (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .filter { isEventCreatedByUser(it) }
                .mapTo(collection) { Pair(MyEventsAdapter.CREATED_CODE, getEventFromJson(it)) }

        addHeader(collection, startIndex, R.string.created_events)
    }

    private fun addHeader(collection: ArrayList<Pair<String, Any>>, startIndex: Int, stringId: Int) {
        if (collection.size > startIndex) {
            collection.add(startIndex, Pair(MyEventsAdapter.HEADER_CODE, view.context.getString(stringId)))
        }
    }

    private fun isEventCreatedByUser(obj: JsonObject): Boolean {
        return AndroidUtilities.getIntFieldFromJson(obj.get("event").asJsonObject.get("creator").asJsonObject.get("id")) ==
                DeviceInfoStore.getUserObject(view.context)?.id
    }

    private fun parseUpcomingEvents(array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        val startIndex = collection.size

        (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .filter { !isEventCreatedByUser(it) && isAfterToday(it) }
                .mapTo(collection) { Pair(MyEventsAdapter.CURRENT_CODE, getEventFromJson(it)) }

        addHeader(collection, startIndex, R.string.current_games)
    }

    private fun isAfterToday(obj: JsonObject): Boolean {
        return DateUtils.isAfter(
                    DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("event").asJsonObject.get("starts_at"))),
                    Date()
               )
    }

    private fun parseClosedEvents(array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        val startIndex = collection.size

        (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .filter { !isEventCreatedByUser(it) && !isAfterToday(it) }
                .mapTo(collection) { Pair(MyEventsAdapter.CLOSED_CODE, getEventFromJson(it)) }

        addHeader(collection, startIndex, R.string.closed_events)
    }
}
