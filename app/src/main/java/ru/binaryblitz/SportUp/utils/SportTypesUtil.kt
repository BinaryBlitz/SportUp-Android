package ru.binaryblitz.SportUp.utils

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.util.Pair

import java.util.ArrayList

import ru.binaryblitz.SportUp.server.ServerConfig

object SportTypesUtil {
    val types = ArrayList<Pair<Int, String>>()
    private val PREF_TYPES = "types"

    fun add(type: Pair<Int, String>) {
        types.add(type)
    }

    fun saveTypes(context: Context) {
        var saveStr = ""

        for (type in types) {
            saveStr += Integer.toString(type.first) + "/" + type.second + "&"
        }

        val prefs = context.getSharedPreferences(ServerConfig.prefsName, Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_TYPES, saveStr).apply()
    }

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(
                ServerConfig.prefsName, Context.MODE_PRIVATE)
        val loadStr = prefs.getString(PREF_TYPES, "null")
        val arr = loadStr!!.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        arr.mapTo(types) { str ->
            Pair(Integer.parseInt(str.split("/".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()[0]),
                    str.split("/".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()[1])
        }
    }
}
