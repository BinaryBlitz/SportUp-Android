package ru.binaryblitz.sportup.server

import com.google.gson.JsonObject

interface JsonObjectResponseListener {
    fun onSuccess(obj: JsonObject)

    fun onError(networkError: String)
}
