package ru.binaryblitz.Chisto.Server

object ServerConfig {
    val baseUrl = "https://chisto.xyz"
    val apiURL = baseUrl + "/api/"

    val imageUrl: String
        get() {
            return ""
        }

    val prefsName = "SportUpPrefs"
    val tokenEntity = "auth_token"
}
