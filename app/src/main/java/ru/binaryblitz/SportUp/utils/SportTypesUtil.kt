package ru.binaryblitz.SportUp.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import ru.binaryblitz.SportUp.R
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

    fun saveTypes(context: Context?) {
        if (context == null) {
            return
        }
        val preferences = context.getSharedPreferences(ServerConfig.preferencesName, Context.MODE_PRIVATE)
        preferences.edit().putStringSet(PREFERENCES_TYPES, sportTypes).apply()
    }

    fun load(context: Context?) {
        if (context == null) {
            return
        }
        val preferences = context.getSharedPreferences(ServerConfig.preferencesName, Context.MODE_PRIVATE)
        val setOfTypes = preferences.getStringSet(PREFERENCES_TYPES, HashSet<String>())

        setOfTypes.map { SportType.fromString(it) }.mapTo(types) { Pair(it.id, it.name!!) }
    }

    fun findColor(context: Context?, id: Int): Int {
        if (context == null) {
            return 0
        }

        val preferences = context.getSharedPreferences(ServerConfig.preferencesName, Context.MODE_PRIVATE)
        val setOfTypes = preferences.getStringSet(PREFERENCES_TYPES, HashSet<String>())

        val types = setOfTypes.map { SportType.fromString(it) }

        return types.firstOrNull { it.id == id }?.color
                ?: ContextCompat.getColor(context, R.color.colorPrimary)
    }

    fun findIcon(context: Context?, id: Int): String? {
        if (context == null) {
            return null
        }

        val preferences = context.getSharedPreferences(ServerConfig.preferencesName, Context.MODE_PRIVATE)
        val setOfTypes = preferences.getStringSet(PREFERENCES_TYPES, HashSet<String>())

        val types = setOfTypes.map { SportType.fromString(it) }

        return types.firstOrNull { it.id == id }?.iconUrl
    }

    fun findName(context: Context?, id: Int): String? {
        if (context == null) {
            return null
        }

        val preferences = context.getSharedPreferences(ServerConfig.preferencesName, Context.MODE_PRIVATE)
        val setOfTypes = preferences.getStringSet(PREFERENCES_TYPES, HashSet<String>())

        val types = setOfTypes.map { SportType.fromString(it) }

        return types.firstOrNull { it.id == id }?.name
    }
}
