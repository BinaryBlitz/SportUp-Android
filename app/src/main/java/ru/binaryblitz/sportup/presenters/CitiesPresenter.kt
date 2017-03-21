package ru.binaryblitz.sportup.presenters

import com.google.gson.JsonArray

import ru.binaryblitz.sportup.activities.SelectCityActivity
import ru.binaryblitz.sportup.adapters.CitiesAdapter
import ru.binaryblitz.sportup.models.City
import ru.binaryblitz.sportup.server.EndpointsService
import ru.binaryblitz.sportup.server.JsonArrayResponseListener
import ru.binaryblitz.sportup.utils.AndroidUtilities
import ru.binaryblitz.sportup.utils.LogUtil

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
                LogUtil.logError(networkError)
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
