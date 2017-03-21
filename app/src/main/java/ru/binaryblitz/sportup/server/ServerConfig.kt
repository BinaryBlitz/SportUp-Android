package ru.binaryblitz.sportup.server

object ServerConfig {
    val baseUrl = "https://sportup-staging.herokuapp.com"
    val apiURL = baseUrl + "/api/"

    val imageUrl: String
        get() {
            return ""
        }

    val prefsName = "SportUpPrefs"
    val tokenEntity = "auth_token"
    val cityEntity = "city_token"
    val userEntity = "auth_info"
}
