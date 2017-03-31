package ru.binaryblitz.SportUp.presenters

import android.graphics.Color
import com.google.gson.JsonArray
import ru.binaryblitz.SportUp.fragments.SportsListFragment
import ru.binaryblitz.SportUp.models.SportType
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonArrayResponseListener
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.LogUtil

class SportTypesPresenter(private val service: EndpointsService, private val view: SportsListFragment) {

    fun getSportTypes(id: Int) {
        service.getSportTypes(id, object : JsonArrayResponseListener {
            override fun onSuccess(array: JsonArray) {
                parseAnswer(array)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
                LogUtil.logError(networkError)
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
                            AndroidUtilities.getStringFieldFromJson(it.get("icon_url")),
                            Color.parseColor(AndroidUtilities.getStringFieldFromJson(it.get("color"))))
                }

        view.onLoaded(collection = collection as ArrayList<SportType>)
    }

}
