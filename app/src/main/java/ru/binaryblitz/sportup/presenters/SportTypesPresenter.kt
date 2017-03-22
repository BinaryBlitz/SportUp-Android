package ru.binaryblitz.sportup.presenters

import android.graphics.Color
import com.google.gson.JsonArray
import ru.binaryblitz.sportup.fragments.SportsListFragment
import ru.binaryblitz.sportup.models.SportType
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.server.JsonArrayResponseListener
import ru.binaryblitz.sportup.server.ServerConfig
import ru.binaryblitz.sportup.utils.AndroidUtilities
import ru.binaryblitz.sportup.utils.LogUtil


class SportTypesPresenter(private val service: EndpointsService, private val view: SportsListFragment) {

    fun getSportTypes() {
        service.getSportTypes(object : JsonArrayResponseListener {
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
                    SportType(AndroidUtilities.getIntFieldFromJson(it.get("id")),
                            AndroidUtilities.getStringFieldFromJson(it.get("name")),
                            AndroidUtilities.getIntFieldFromJson(it.get("events_count")),
                            3,
                            ServerConfig.imageUrl + AndroidUtilities.getStringFieldFromJson(it.get("icon_url")),
                            Color.parseColor("#212121")) //Color.parseColor(AndroidUtilities.getStringFieldFromJson(it.get("color"))))
                }

        view.onLoaded(collection = collection as ArrayList<SportType>)
    }

}
