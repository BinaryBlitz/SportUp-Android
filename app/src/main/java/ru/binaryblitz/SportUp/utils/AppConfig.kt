package ru.binaryblitz.SportUp.utils

import android.content.Context
import android.content.Intent
import ru.binaryblitz.SportUp.activities.RegistrationActivity
import ru.binaryblitz.SportUp.server.DeviceInfoStore

object AppConfig {
    val LogTag = "SportUp"

    fun checkIfUserLoggedIn(context: Context): Boolean {
        if (DeviceInfoStore.getToken(context) == "null") {
            val intent = Intent(context, RegistrationActivity::class.java)
            context.startActivity(intent)
            return false
        }

        return true
    }
}
