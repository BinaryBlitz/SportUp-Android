package ru.binaryblitz.SportUp.presenters

import android.util.Pair
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.activities.UserListActivity
import ru.binaryblitz.SportUp.adapters.PlayersAdapter
import ru.binaryblitz.SportUp.models.Player
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonArrayResponseListener
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.LogUtil
import java.util.*

class PlayersPresenter(private val service: EndpointsService, private val view: UserListActivity) {

    fun getTeams(id: Int, token: String, userLimit: Int) {
        service.getTeams(id, token, object : JsonArrayResponseListener {
            override fun onSuccess(array: JsonArray) {
                parseAnswer(array, userLimit)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
            }
        })
    }

    private fun parseAnswer(array: JsonArray, userLimit: Int) {
        view.onLoaded(collection = parseTeamNumbers(userLimit, array))
    }

    private fun parseTeamNumbers(userLimit: Int, array: JsonArray): ArrayList<Pair<String, Any>> {
        val collection: ArrayList<Pair<String, Any>> = ArrayList()

        val teams = (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .distinct()
                .map { AndroidUtilities.getIntFieldFromJson(it.get("user").asJsonObject.get("team_number")) }


        for (team in teams) {
            addPlayersToTeam(team, userLimit, array, collection)
        }

        return collection
    }

    private fun addPlayersToTeam(teamNumber: Int, userLimit: Int, array: JsonArray, collection: ArrayList<Pair<String, Any>>) {
        val startIndex = collection.size

        (0..array.size() - 1)
                .map { array.get(it).asJsonObject }
                .filter { AndroidUtilities.getIntFieldFromJson(it.get("user").asJsonObject.get("team_number")) == teamNumber }
                .mapTo(collection) {
                    if (AndroidUtilities.getIntFieldFromJson(it.get("user").asJsonObject.get("id"))
                            == DeviceInfoStore.getUserObject(view)?.id) {
                        Pair(PlayersAdapter.ME_CODE, getPlayerFromJson(it, teamNumber))
                    } else {
                        Pair(PlayersAdapter.BASIC_CODE, getPlayerFromJson(it, teamNumber))
                    }
                }

        collection.add(startIndex, Pair(PlayersAdapter.HEADER_CODE, createPlayer(teamNumber, userLimit, 0)))
    }

    private fun createPlayer(teamNumber: Int, userLimit: Int, userCount: Int): Player {
        if (teamNumber == 0) {
            return Player(1, view.getString(R.string.no_team), "", 0, 0, 0, "")
        } else {
            return Player(1, "Команда $teamNumber", "$userCount / $userLimit", 0, 0, 0, "")
        }
    }

    private fun getPlayerFromJson(obj: JsonObject, teamNumber: Int): Player {
        return Player(
                AndroidUtilities.getIntFieldFromJson(obj.get("user").asJsonObject.get("id")),
                AndroidUtilities.getStringFieldFromJson(obj.get("user").asJsonObject.get("first_name")) + " " +
                        AndroidUtilities.getStringFieldFromJson(obj.get("user").asJsonObject.get("last_name")),
                if (teamNumber == 0) "" else "Нападающий",
                AndroidUtilities.getIntFieldFromJson(obj.get("user").asJsonObject.get("violations_count")),
                AndroidUtilities.getIntFieldFromJson(obj.get("id")),
                AndroidUtilities.getIntFieldFromJson(obj.get("team_number")),
                AndroidUtilities.getUrlFieldFromJson(obj.get("user").asJsonObject.get("avatar_url"))
        )
    }
}
