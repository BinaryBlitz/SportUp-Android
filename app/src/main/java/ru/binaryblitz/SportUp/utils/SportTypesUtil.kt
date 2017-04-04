package ru.binaryblitz.SportUp.utils

import android.content.Context
import android.support.v4.util.Pair
import ru.binaryblitz.SportUp.models.SportType
import ru.binaryblitz.SportUp.server.ServerConfig
import java.util.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.map
import kotlin.collections.mapTo

object SportTypesUtil {
    val types = ArrayList<Pair<Int, String>>()
    val sportTypes = HashSet<String>()
    private val PREFERENCES_TYPES = "types"

    fun add(type: String) {
        sportTypes.add(type)
    }

    fun saveTypes(context: Context) {
        val preferences = context.getSharedPreferences(ServerConfig.preferencesName, Context.MODE_PRIVATE)
        preferences.edit().putStringSet(PREFERENCES_TYPES, sportTypes).apply()
    }

    fun load(context: Context) {
        val preferences = context.getSharedPreferences(ServerConfig.preferencesName, Context.MODE_PRIVATE)
        val setOfTypes = preferences.getStringSet(PREFERENCES_TYPES, HashSet<String>())

        setOfTypes.map { SportType.fromString(it) }
                .mapTo(types) { Pair(it.id, it.name!!) }
    }
}
