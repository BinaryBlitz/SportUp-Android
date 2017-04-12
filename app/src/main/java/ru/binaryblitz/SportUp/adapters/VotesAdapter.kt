package ru.binaryblitz.SportUp.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.activities.VotesActivity
import ru.binaryblitz.SportUp.models.Player
import ru.binaryblitz.SportUp.utils.Image
import java.util.*

class VotesAdapter(private val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var players: ArrayList<Player>

    init {
        players = ArrayList<Player>()
    }

    fun setCollection(players: ArrayList<Player>) {
        this.players = players
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_vote, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder

        val player = players[position]

        holder.name.text = player.name
        Image.loadAvatar(context, player.name, player.avatarUrl, holder.avatar)

        holder.itemView.setOnClickListener {
            (context as VotesActivity).vote(player.id)
        }
    }

    override fun getItemCount(): Int {
        return players.size
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name = itemView.findViewById(R.id.name) as TextView
        internal var avatar = itemView.findViewById(R.id.avatar) as ImageView
    }
}
