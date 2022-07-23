package ru.binaryblitz.SportUp.utils

import android.util.Log

object LogUtil {
    fun logError(logMessage: String) {
        Log.e(AppConfig.LogTag, logMessage)
    }

    fun logError(logMessage: ByteArray) {
        var res = ""

        for (aLogMessage in logMessage) {
            res += java.lang.Byte.toString(aLogMessage) + " , "
        }

        Log.e(AppConfig.LogTag, res)
    }

    fun logError(logMessage: Int?) {
        Log.e(AppConfig.LogTag, "value: " + logMessage!!)
    }

    fun logError(logMessage: Double?) {
        Log.e(AppConfig.LogTag, "value: " + logMessage!!)
    }

    fun logInformation(logMessage: String) {
        Log.i(AppConfig.LogTag, logMessage)
    }

    fun logInformation(logMessage: Int?) {
        Log.i(AppConfig.LogTag, "value: " + logMessage!!)
    }

    fun logInformation(logMessage: Double?) {
        Log.i(AppConfig.LogTag, "value: " + logMessage!!)
    }

    fun logException(ex: Exception) {
        Log.e(AppConfig.LogTag, ex.localizedMessage)
        ex.printStackTrace()
    }
}
