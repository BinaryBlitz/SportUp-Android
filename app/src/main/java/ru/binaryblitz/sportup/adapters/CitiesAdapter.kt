package ru.binaryblitz.sportup.adapters

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.binaryblitz.sportup.R
import ru.binaryblitz.sportup.models.User
import ru.binaryblitz.sportup.server.DeviceInfoStore
import java.util.*

class CitiesAdapter(private val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class City(val city: ru.binaryblitz.sportup.models.City, var selected: Boolean)

    private var collection = ArrayList<City>()

    fun getCities(): ArrayList<City> {
        return collection
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder
        val city = collection[position].city
        holder.name.text = city.name

        if (collection[position].selected) {
            setSelected(holder)
        } else {
            setDeselected(holder)
        }

        holder.itemView.setOnClickListener {
//            val intent = Intent(context, OrdersActivity::class.java)
//            DeviceInfoStore.saveCity(context, city)
//            if (DeviceInfoStore.getToken(context) == "null") {
//                saveUser(city)
//            }
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            context.startActivity(intent)
//            context.finish()
        }
    }

    private fun setSelected(holder: ViewHolder) {
        holder.name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        holder.marker.visibility = View.VISIBLE
    }

    private fun setDeselected(holder: ViewHolder) {
        holder.name.setTextColor(ContextCompat.getColor(context, R.color.greyColor))
        holder.marker.visibility = View.GONE
    }

    private fun saveUser(city: ru.binaryblitz.sportup.models.City) {
        val user = User.createDefault()
        user.city = city.name
        DeviceInfoStore.saveUser(context, user)
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    fun setCollection(collection: List<City>) {
        this.collection = collection as ArrayList<City>
    }

    fun selectCity(latitude: Double, longitude: Double) {
        var position = 0
        var min = Float.MAX_VALUE
        for (i in collection.indices) {
            val dist = distanceBetween(collection[i].city.latitude, collection[i].city.longitude, latitude, longitude)

            if (dist < min) {
                position = i
                min = dist
            }
        }

        collection[position].selected = true
        val city = collection[0]
        collection[0] = collection[position]
        collection[position] = city

        notifyDataSetChanged()
    }

    fun distanceBetween(firstLatitude: Double, firstLongitude: Double, secondLatitude: Double, secondLongitude: Double): Float {
        val startPoint = Location("")
        val endPoint = Location("")

        startPoint.latitude = firstLatitude
        startPoint.longitude = firstLongitude
        endPoint.latitude = secondLatitude
        endPoint.longitude = secondLongitude

        return startPoint.distanceTo(endPoint)
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById(R.id.name) as TextView
        val marker = itemView.findViewById(R.id.marker) as ImageView
    }
}
