package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonArray

import ru.binaryblitz.SportUp.activities.SelectCityActivity
import ru.binaryblitz.SportUp.adapters.CitiesAdapter
import ru.binaryblitz.SportUp.models.City
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonArrayResponseListener
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.LogUtil

class CitiesPresenter(private val service: EndpointsService, private val view: SelectCityActivity) {

    fun getCityList() {
        view.showLoadingIndicator()

        service.getCityList(object : JsonArrayResponseListener {
            override fun onSuccess(array: JsonArray) {
                parseAnswer(array)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
                view.hideLoadingIndicator()
            }
        })
    }

    private fun parseAnswer(array: JsonArray) {
        val collection = (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .map {
                    City(AndroidUtilities.getIntFieldFromJson(it.get("id")),
                            AndroidUtilities.getStringFieldFromJson(it.get("name")),
                            AndroidUtilities.getDoubleFieldFromJson(it.get("latitude")),
                            AndroidUtilities.getDoubleFieldFromJson(it.get("longitude")))
                }
                .map { CitiesAdapter.City(it, false) }

        view.hideLoadingIndicator()
        view.onLoaded(collection = collection as ArrayList<CitiesAdapter.City>)
    }

}
