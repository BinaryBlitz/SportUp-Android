package ru.binaryblitz.SportUp.adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.activities.EventActivity
import ru.binaryblitz.SportUp.fragments.UserEventsFragment
import ru.binaryblitz.SportUp.models.MyEvent
import ru.binaryblitz.SportUp.utils.AppConfig
import ru.binaryblitz.SportUp.utils.DateUtils
import ru.binaryblitz.SportUp.utils.Image
import java.util.*

class MyEventsAdapter(private val context: UserEventsFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val EXTRA_ID = "id"
    val EXTRA_COLOR = "color"
  
    private var collection = ArrayList<Pair<String, Any>>()

    fun setCollection(collection: ArrayList<Pair<String, Any>>) {
        this.collection = collection
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER) {
            return createHeaderViewHolder(parent)
        } else {
            return createBasicViewHolder(parent, getLayoutId(viewType))
        }
    }

    private fun getLayoutId(viewType: Int): Int {
        when (viewType) {
            CREATED -> return R.layout.item_created_event
            CURRENT -> return R.layout.item_current_event
            CLOSED -> return R.layout.item_closed_event
            INVITE -> return R.layout.item_invite
            else -> return R.layout.item_created_event
        }
    }

    private fun createBasicViewHolder(parent: ViewGroup, layout: Int): BasicViewHolder {
        return BasicViewHolder(createView(parent, layout))
    }

    private fun createHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        return HeaderViewHolder(createView(parent, R.layout.item_my_events_header))
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
        Image.loadPhoto(event.icon, holder.icon)
        holder.icon.setColorFilter(event.color)

        holder.itemView.setOnClickListener {
            if (event.password != null) {
                context.showPasswordDialog(event.password, event.eventId)
                return@setOnClickListener
            }
            if (!AppConfig.checkIfUserLoggedIn(context.activity)) {
                return@setOnClickListener
            }
            val intent = Intent(context.activity, EventActivity::class.java)
            EventActivity.sportTypeId = event.sportTypeId
            intent.putExtra(EXTRA_ID, event.eventId)
            intent.putExtra(EXTRA_COLOR, event.color)
            context.startActivity(intent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (collection[position].first) {
            HEADER_CODE -> return HEADER
            CREATED_CODE -> return CREATED
            CURRENT_CODE -> return CURRENT
            CLOSED_CODE -> return CLOSED
            else -> return INVITE
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

        val HEADER_CODE = "H"
        val CREATED_CODE = "CR"
        val CURRENT_CODE = "CU"
        val CLOSED_CODE = "CL"
        val INVITE_CODE = "I"
    }
}
