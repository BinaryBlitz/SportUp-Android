package ru.binaryblitz.SportUp.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.models.MyEvent
import ru.binaryblitz.SportUp.utils.DateUtils
import ru.binaryblitz.SportUp.utils.Image
import java.util.*

class MyEventsAdapter(private val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var collection = ArrayList<Pair<String, Any>>()

    fun setCollection(collection: ArrayList<Pair<String, Any>>) {
        this.collection = collection
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            HEADER -> {
                return HeaderViewHolder(createView(parent, R.layout.item_my_events_header))
            }
            CREATED -> {
                return BasicViewHolder(createView(parent, R.layout.item_created_event))
            }
            CURRENT -> {
                return BasicViewHolder(createView(parent, R.layout.item_current_event))
            }
            CLOSED -> {
                return BasicViewHolder(createView(parent, R.layout.item_closed_event))
            }
            INVITE -> {
                return BasicViewHolder(createView(parent, R.layout.item_invite))
            }
            else -> {
                return HeaderViewHolder(createView(parent, R.layout.item_my_events_header))
            }
        }
    }

    private fun createView(parent: ViewGroup, layout: Int): View {
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HEADER) {
            bindHeader(viewHolder as HeaderViewHolder , (collection[position].second as String))
        } else {
            bindBasic(position, viewHolder as BasicViewHolder)
        }
    }

    private fun bindHeader(view: HeaderViewHolder, name: String) {
        view.name.text = name
    }

    private fun bindBasic(position: Int, holder: BasicViewHolder) {
        val event = collection[position].second as MyEvent
        holder.name.text = event.name
        holder.date.text = DateUtils.getDateStringRepresentationWithoutTime(event.startsAt)
        holder.time.text = DateUtils.getTimeStringRepresentation(event.startsAt)
        Image.loadPhoto(context, event.icon, holder.icon)
        holder.icon.setColorFilter(event.color)
    }

    override fun getItemViewType(position: Int): Int {
        if (collection[position].first == "H") {
            return HEADER
        } else if (collection[position].first == "CR") {
            return CREATED
        } else if (collection[position].first == "CU") {
            return CURRENT
        } else if (collection[position].first == "CL") {
            return CLOSED
        } else {
            return INVITE
        }
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    private inner class HeaderViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById(R.id.header) as TextView
    }

    private inner class BasicViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById(R.id.name) as TextView
        val date = itemView.findViewById(R.id.date) as TextView
        val time = itemView.findViewById(R.id.startsAt) as TextView
        val icon = itemView.findViewById(R.id.category_icon) as ImageView
    }

    companion object {
        private val HEADER = 1
        private val CREATED = 2
        private val CURRENT = 3
        private val CLOSED = 4
        private val INVITE = 5
    }
}
