package ru.binaryblitz.sportup.adapters

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.activities.SportEventsActivity
import ru.binaryblitz.sportup.models.SportType
import ru.binaryblitz.sportup.utils.Image
import java.util.*

class SportTypesAdapter(private val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var sportTypes: ArrayList<SportType>? = null

    val EXTRA_COLOR = "color"
    val EXTRA_ID = "id"
    val EXTRA_NAME = "name"

    init {
        sportTypes = ArrayList<SportType>()
    }

    fun setCollection(sportTypes: ArrayList<SportType>) {
        this.sportTypes = sportTypes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sport_type, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder

        val sportType = sportTypes!![position]

        holder.name.text = sportType.name
        holder.description.text = generateDescription(sportType)

        Image.loadPhoto(context, sportType.iconUrl, holder.icon)
        holder.icon.setColorFilter(sportType.color)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SportEventsActivity::class.java)
            intent.putExtra(EXTRA_ID, sportType.id)
            intent.putExtra(EXTRA_COLOR, sportType.color)
            intent.putExtra(EXTRA_NAME, sportType.name)
            context.startActivity(intent)
        }
    }

    private fun generateDescription(sportType: SportType): String {
        val gamesText = context.resources.getQuantityString(R.plurals.events,
                sportType.gamesQuantity, sportType.gamesQuantity)
        val placesText = context.resources.getQuantityString(R.plurals.places,
                sportType.placesQuantity, sportType.placesQuantity)

        return gamesText + " - " + placesText
    }

    override fun getItemCount(): Int {
        return sportTypes!!.size
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name = itemView.findViewById(R.id.name) as TextView
        internal var description = itemView.findViewById(R.id.description) as TextView
        internal var icon = itemView.findViewById(R.id.category_icon) as ImageView
    }
}
