package ru.binaryblitz.SportUp.presenters

import android.graphics.Color
import android.util.Pair
import com.google.gson.JsonArray
import ru.binaryblitz.SportUp.R
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

    private fun parseInvites(array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        if (array.size() != 0) {
            collection.add(Pair("H", view.context.getString(R.string.invite_code)))

            (0..array.size())
                    .map { array.get(it).asJsonObject }
                    .mapTo(collection) {
                        Pair("I", MyEvent(
                                AndroidUtilities.getIntFieldFromJson(it.get("id")),
                                AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("name")),
                                DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("starts_at"))),
                                DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("ends_at"))),
                                AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("sport_type").asJsonObject.get("icon_url")),
                                Color.parseColor(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("sport_type").asJsonObject.get("color")))
                        ))
                    }
        }
    }

    private fun parseCreatedEvents(array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        val startIndex = collection.size

        (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .filter {
                    AndroidUtilities.getIntFieldFromJson(it.get("event").asJsonObject.get("creator").asJsonObject.get("id")) ==
                            DeviceInfoStore.getUserObject(view.context)?.id
                }
                .mapTo(collection) {
                    Pair("CR", MyEvent(
                            AndroidUtilities.getIntFieldFromJson(it.get("id")),
                            AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("name")),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("starts_at"))),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("ends_at"))),
                            AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("sport_type").asJsonObject.get("icon_url")),
                            Color.parseColor(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("sport_type").asJsonObject.get("color")))
                    ))
                }

        if (collection.size > startIndex) {
            collection.add(startIndex, Pair("H", view.context.getString(R.string.created_events)))
        }
    }

    private fun parseUpcomingEvents(array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        val startIndex = collection.size

        (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .filter {
                    AndroidUtilities.getIntFieldFromJson(it.get("event").asJsonObject.get("creator").asJsonObject.get("id")) !=
                            DeviceInfoStore.getUserObject(view.context)?.id &&
                    DateUtils.isAfter(
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("starts_at"))),
                            Date()
                    )
                }
                .mapTo(collection) {
                    Pair("CU", MyEvent(
                            AndroidUtilities.getIntFieldFromJson(it.get("id")),
                            AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("name")),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("starts_at"))),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("ends_at"))),
                            AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("sport_type").asJsonObject.get("icon_url")),
                            Color.parseColor(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("sport_type").asJsonObject.get("color")))
                    ))
                }

        if (collection.size > startIndex) {
            collection.add(startIndex, Pair("H", view.context.getString(R.string.current_games)))
        }
    }

    private fun parseClosedEvents(array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        val startIndex = collection.size

        (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .filter {
                    AndroidUtilities.getIntFieldFromJson(it.get("event").asJsonObject.get("creator").asJsonObject.get("id")) !=
                            DeviceInfoStore.getUserObject(view.context)?.id &&
                    DateUtils.isAfter(
                            Date(),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("starts_at")))
                    )
                }
                .mapTo(collection) {
                    Pair("CL", MyEvent(
                            AndroidUtilities.getIntFieldFromJson(it.get("id")),
                            AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("name")),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("starts_at"))),
                            DateUtils.parse(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("ends_at"))),
                            AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("sport_type").asJsonObject.get("icon_url")),
                            Color.parseColor(AndroidUtilities.getStringFieldFromJson(it.get("event").asJsonObject.get("sport_type").asJsonObject.get("color")))
                    ))
                }

        if (collection.size > startIndex) {
            collection.add(startIndex, Pair("H", view.context.getString(R.string.closed_events)))
        }
    }
}
