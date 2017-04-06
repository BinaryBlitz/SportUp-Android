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
import ru.binaryblitz.SportUp.models.Player
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.utils.Image
import java.util.*

class PlayersAdapter(private val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var collection = ArrayList<Pair<String, Any>>()

    fun setCollection(collection: ArrayList<Pair<String, Any>>) {
        this.collection = collection
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER) {
            return createHeaderViewHolder(parent)
        } else {
            return createBasicViewHolder(parent, getLayoutId(viewType), viewType)
        }
    }

    private fun getLayoutId(viewType: Int): Int {
        when (viewType) {
            BASIC -> return R.layout.item_basic_player
            ME -> return R.layout.item_player_me
            else -> return R.layout.item_player_me
        }
    }

    private fun createBasicViewHolder(parent: ViewGroup, layout: Int, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ME) {
            return MeViewHolder(createView(parent, layout))
        } else {
            return BasicViewHolder(createView(parent, layout))
        }
    }

    private fun createHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        return HeaderViewHolder(createView(parent, R.layout.item_players_header))
    }

    private fun createView(parent: ViewGroup, layout: Int): View {
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HEADER) {
            bindHeader(viewHolder as HeaderViewHolder , (collection[position].second as Player))
        } else if (getItemViewType(position) == BASIC) {
            bindBasic(position, viewHolder as BasicViewHolder)
        } else {
            bindMe(position, viewHolder as MeViewHolder)
        }
    }

    private fun bindHeader(view: HeaderViewHolder, information: Player) {
        view.name.text = information.name
        view.headerInformation.text = information.role
    }

    private fun bindMe(position: Int, holder: MeViewHolder) {
        val player = collection[position].second as Player
        holder.name.text = player.name
        holder.role.text = player.role
        holder.role.visibility = if (player.role.isEmpty()) View.GONE else View.VISIBLE
        Image.loadAvatar(context, player.name, player.avatarUrl, holder.avatar)
    }

    private fun bindBasic(position: Int, holder: BasicViewHolder) {
        val player = collection[position].second as Player
        holder.name.text = player.name
        holder.role.text = player.role
        Image.loadAvatar(context, player.name, player.avatarUrl, holder.avatar)
        holder.role.visibility = if (player.role.isEmpty()) View.GONE else View.VISIBLE
        if (player.id == DeviceInfoStore.getUserObject(context)?.id) {
            return
        }

        holder.violationsCount.text = player.violationsCount.toString()
        holder.itemView.findViewById(R.id.violationsView).visibility = if (player.violationsCount == 0) View.GONE else View.VISIBLE
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
        val headerInformation = itemView.findViewById(R.id.headerInformation) as TextView
    }

    private inner class MeViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById(R.id.name) as TextView
        val role = itemView.findViewById(R.id.role) as TextView
        val avatar = itemView.findViewById(R.id.avatar) as ImageView
    }

    private inner class BasicViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById(R.id.name) as TextView
        val role = itemView.findViewById(R.id.role) as TextView
        val violationsCount = itemView.findViewById(R.id.violationsCount) as TextView
        val avatar = itemView.findViewById(R.id.avatar) as ImageView
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
