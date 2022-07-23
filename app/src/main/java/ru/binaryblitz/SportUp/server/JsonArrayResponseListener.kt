package ru.binaryblitz.SportUp.server

import com.google.gson.JsonArray

interface JsonArrayResponseListener {
    fun onSuccess(array: JsonArray)

    fun onError(networkError: String)
}
