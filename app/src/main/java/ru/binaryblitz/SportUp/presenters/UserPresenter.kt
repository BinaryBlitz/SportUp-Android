package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.MainActivity
import ru.binaryblitz.SportUp.models.User
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonObjectResponseListener
import ru.binaryblitz.SportUp.utils.AndroidUtilities

class UserPresenter(private val service: EndpointsService, private val view: MainActivity) {

    fun getUser(token: String) {
        service.getUser(token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseAnswer(obj)
            }

            override fun onError(networkError: String) {
            }
        })
    }

    private fun parseAnswer(obj: JsonObject) {
        var user = DeviceInfoStore.getUserObject(view)

        if (user == null) {
            user = User.createDefault()
        }

        user.id = AndroidUtilities.getIntFieldFromJson(obj.get("id"))
        user.firstName = AndroidUtilities.getStringFieldFromJson(obj.get("first_name"))
        user.lastName = AndroidUtilities.getStringFieldFromJson(obj.get("last_name"))
        user.avatarUrl = AndroidUtilities.getUrlFieldFromJson(obj.get("avatar_url"))
        DeviceInfoStore.saveUser(view, user)
    }
}