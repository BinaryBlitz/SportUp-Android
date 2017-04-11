package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.VotesActivity
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonObjectResponseListener

class VotePresenter(private val service: EndpointsService, private val view: VotesActivity) {

    fun vote(vote: JsonObject, id: Int, token: String) {
        service.vote(vote, id, token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseAnswer(obj)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    private fun parseAnswer(obj: JsonObject) {
        view.onLoaded(obj)
    }

}