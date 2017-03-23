package ru.binaryblitz.sportup.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.models.Event
import ru.binaryblitz.sportup.utils.DateUtils
import java.util.*

class EventsAdapter(private val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var events: ArrayList<Event>

    init {
        events = ArrayList<Event>()
    }

    fun setCollection(events: ArrayList<Event>) {
        this.events = events
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder

        val event = events[position]

        holder.name.text = event.name
        holder.address.text = event.address
        holder.startsAt.text = DateUtils.getTimeStringRepresentation(event.startsAt)
        holder.userLimit.text = event.userLimit.toString() + " / " + event.teamLimit.toString()
        holder.price.text = event.price.toString() + context.getString(R.string.ruble_sign)

        holder.isPublic.visibility = if (event.isPublic) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return events.size
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name = itemView.findViewById(R.id.name) as TextView
        internal var address = itemView.findViewById(R.id.address) as TextView
        internal var startsAt = itemView.findViewById(R.id.startsAt) as TextView
        internal var userLimit = itemView.findViewById(R.id.userLimit) as TextView
        internal var isPublic = itemView.findViewById(R.id.isPublic) as ImageView
        internal var price = itemView.findViewById(R.id.price) as TextView
    }
}