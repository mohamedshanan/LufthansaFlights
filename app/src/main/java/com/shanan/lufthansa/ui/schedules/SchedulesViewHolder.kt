package com.shanan.lufthansa.ui.schedules

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shanan.lufthansa.R
import com.shanan.lufthansa.model.Schedule
import com.shanan.lufthansa.ui.map.MapActivity
import com.shanan.lufthansa.utils.Constants


/**
 * View Holder for a [Schedule] RecyclerView list item.
 */
class SchedulesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val stopsTv: TextView = view.findViewById(R.id.stops)
    private val operator: TextView = view.findViewById(R.id.flightNumber)
    private val totalTime: TextView = view.findViewById(R.id.totalTime)

    private var schedule: Schedule? = null

    fun showScheduleData(schedule: Schedule) {

        this.schedule = schedule
        operator.text = schedule.flight.get(0).marketingCarrier.airlineID
        val airportsList: MutableList<String> = ArrayList()

        val stops = StringBuilder()
        stops.append(schedule.flight.get(0).departure.airportCode)
        airportsList.add(schedule.flight.get(0).departure.airportCode)

        for (i in 0..(schedule.flight.size.minus(1))) {
            stops.append(" - ")
            stops.append(schedule.flight.get(i).arrival.airportCode)
            airportsList.add(schedule.flight.get(i).arrival.airportCode)
        }

        stopsTv.text = stops.toString()

        totalTime.text = schedule.totalJourney?.duration?.replace("PT", "")?.replace("D", "D ")?.replace("H", "H ")


        itemView.setOnClickListener {
            schedule.flight.let {
                val intent = Intent(itemView.context, MapActivity::class.java)
                intent.putStringArrayListExtra(Constants.IntentPassing.AIRPORTS_CODES, ArrayList(airportsList))
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): SchedulesViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_flight, parent, false)
            return SchedulesViewHolder(view)
        }
    }
}