package ru.binaryblitz.SportUp.server

import com.google.gson.JsonObject

interface JsonObjectResponseListener {
    fun onSuccess(obj: JsonObject)

    fun onError(networkError: String)
}
