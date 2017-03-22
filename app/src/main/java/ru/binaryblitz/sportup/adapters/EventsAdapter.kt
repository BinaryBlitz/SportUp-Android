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

    private var games: ArrayList<Event>
    init {
        games = ArrayList<Event>()
    }

    fun setCollection(events: ArrayList<Event>) {
        this.games = events
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_game_event, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder

        val game = games[position]

        holder.name.text = game.name
        holder.address.text = game.address
        holder.endsAt.text = DateUtils.getTimeStringRepresentation(game.endsAt)
        holder.userLimit.text = game.userLimit.toString() + " / " + game.teamLimit.toString()
        holder.price.text = game.price.toString() + context.getString(R.string.ruble_sign)

        holder.isPublic.visibility = if (game.isPublic) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return games.size
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name = itemView.findViewById(R.id.name) as TextView
        internal var address = itemView.findViewById(R.id.address) as TextView
        internal var endsAt = itemView.findViewById(R.id.endsAt) as TextView
        internal var userLimit = itemView.findViewById(R.id.userLimit) as TextView
        internal var isPublic = itemView.findViewById(R.id.isPublic) as ImageView
        internal var price = itemView.findViewById(R.id.price) as TextView
    }
}
