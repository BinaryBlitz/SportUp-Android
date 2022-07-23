package ru.binaryblitz.SportUp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.models.SportType
import ru.binaryblitz.SportUp.server.ServerConfig
import java.util.ArrayList
import kotlin.collections.HashSet

object SportTypesUtil {
    val types = ArrayList<Pair<Int, String>>()
    val sportTypes = HashSet<String>()
    private val PREFERENCES_TYPES = "types"

    fun add(type: String) {
        sportTypes.add(type)
    }

    fun getMarker(id: Int, context: Context, icon: Bitmap): Bitmap {
        val view = LayoutInflater.from(context).inflate(R.layout.default_marker, null)
        view.isDrawingCacheEnabled = true

        (view.findViewById(R.id.markerBackground) as ImageView).setColorFilter(findColor(context, id))
        (view.findViewById(R.id.sportTypeIcon) as ImageView).setColorFilter(Color.parseColor("#ffffff"))
        (view.findViewById(R.id.sportTypeIcon) as ImageView).setImageBitmap(icon)

        view.buildDrawingCache()

        return getViewBitmap(view, context)
    }

    fun getViewBitmap(view: View, context: Context): Bitmap {
        val width = AndroidUtilities.dpToPx(context, 56f)
        val height = AndroidUtilities.dpToPx(context, 56f)

        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        view.draw(canvas)

        return bitmap
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
