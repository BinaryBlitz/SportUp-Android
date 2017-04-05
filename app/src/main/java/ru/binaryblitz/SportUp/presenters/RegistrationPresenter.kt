package ru.binaryblitz.SportUp.presenters

import com.google.gson.JsonObject
import ru.binaryblitz.SportUp.activities.RegistrationActivity
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.server.JsonObjectResponseListener
import ru.binaryblitz.SportUp.utils.LogUtil

class RegistrationPresenter(private val service: EndpointsService, private val view: RegistrationActivity) {

    fun auth(body: JsonObject) {
        service.auth(body, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseAuth(obj)
            }

            override fun onError(networkError: String) {
                view.onInternetConnectionError()
                view.dismissProgress()
            }
        })
    }

    fun verify(body: JsonObject, token: String) {
        service.verify(body, token, object : JsonObjectResponseListener {
            override fun onSuccess(obj: JsonObject) {
                parseVerify(obj)
            }

            override fun onError(networkError: String) {
                view.showCodeError()
                view.dismissProgress()
                LogUtil.logError(networkError)
            }
        })
    }

    fun parseAuth(body: JsonObject) {
        view.onAuthResponse(body)
    }

    fun parseVerify(body: JsonObject) {
        view.onVerifyAnswer(body)
    }
}