package ru.binaryblitz.SportUp.adapters

import android.app.Activity
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
import ru.binaryblitz.SportUp.models.MyEvent
import ru.binaryblitz.SportUp.utils.AppConfig
import ru.binaryblitz.SportUp.utils.DateUtils
import ru.binaryblitz.SportUp.utils.Image
import java.util.*

class PlayersAdapter(private val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            BASIC -> return R.layout.item_basic_player
            ME -> return R.layout.item_player_me
            else -> return R.layout.item_player_me
        }
    }

    private fun createBasicViewHolder(parent: ViewGroup, layout: Int): BasicViewHolder {
        return BasicViewHolder(createView(parent, layout))
    }

    private fun createHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        return HeaderViewHolder(createView(parent, R.layout.item_players_header))
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
            if (!AppConfig.checkIfUserLoggedIn(context)) {
                return@setOnClickListener
            }
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(EXTRA_ID, event.eventId)
            intent.putExtra(EXTRA_COLOR, event.color)
            context.startActivity(intent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (collection[position].first) {
            HEADER_CODE -> return HEADER
            BASIC_CODE -> return BASIC
            ME_CODE -> return ME
            else -> return HEADER
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
        val date = itemView.findViewById(R.id.role) as TextView
        val time = itemView.findViewById(R.id.startsAt) as TextView
        val icon = itemView.findViewById(R.id.category_icon) as ImageView
    }

    companion object {
        private val HEADER = 1
        private val BASIC = 2
        private val ME = 3

        val HEADER_CODE = "H"
        val BASIC_CODE = "B"
        val ME_CODE = "M"
    }
}
