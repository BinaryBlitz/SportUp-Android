package ru.binaryblitz.SportUp.server

import android.content.Context
import android.content.SharedPreferences
import ru.binaryblitz.SportUp.models.City
import ru.binaryblitz.SportUp.models.User
import ru.binaryblitz.SportUp.utils.LogUtil

object DeviceInfoStore {
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        prefs.edit().putString(ServerConfig.tokenEntity, token).apply()
    }

    fun resetToken(context: Context) {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        prefs.edit().putString(ServerConfig.tokenEntity, "null").apply()
    }

    fun getToken(context: Context): String {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        return prefs.getString(ServerConfig.tokenEntity, "null")
    }

    fun getCityObject(context: Context): City? {
        try {
            return City.fromString(DeviceInfoStore.getCity(context))
        } catch (e: Exception) {
            return null
        }
    }

    fun saveUser(context: Context, user: User) {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        prefs.edit().putString(ServerConfig.userEntity, user.asString()).apply()
    }

    fun getUserObject(context: Context): User? {
        try {
            return User.fromString(DeviceInfoStore.getUser(context))
        } catch (e: Exception) {
            LogUtil.logException(e)
            return null
        }
    }

    fun getUser(context: Context): String {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        return prefs.getString(ServerConfig.userEntity, "null")
    }

    fun resetUser(context: Context) {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        prefs.edit().putString(ServerConfig.userEntity, "null").apply()
    }

    fun saveCity(context: Context, city: City) {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        prefs.edit().putString(ServerConfig.cityEntity, city.asString()).apply()
    }


    fun getCity(context: Context): String {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        return prefs.getString(ServerConfig.cityEntity, "null")
    }

    fun resetCity(context: Context) {
        val prefs = context.getSharedPreferences(
                ServerConfig.preferencesName, Context.MODE_PRIVATE)
        prefs.edit().putString(ServerConfig.cityEntity, "null").apply()
    }
}
