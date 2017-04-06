package ru.binaryblitz.SportUp.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.activities.EventActivity
import ru.binaryblitz.SportUp.activities.SportEventsActivity
import ru.binaryblitz.SportUp.fragments.UserEventsFragment
import ru.binaryblitz.SportUp.models.Event
import ru.binaryblitz.SportUp.utils.DateUtils
import java.util.*

class EventsAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val EXTRA_ID = "id"
    val EXTRA_COLOR = "color"

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

        holder.itemView.setOnClickListener {
            if (event.password != null) {
                (context as SportEventsActivity).showPasswordDialog(event.password, event.id)
            }
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(EXTRA_ID, event.id)
            intent.putExtra(EXTRA_COLOR, SportEventsActivity.color)
            context.startActivity(intent)
        }
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
