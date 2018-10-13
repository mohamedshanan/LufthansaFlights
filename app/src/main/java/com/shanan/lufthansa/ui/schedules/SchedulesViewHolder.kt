package com.shanan.lufthansa.ui.schedules

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shanan.lufthansa.R
import com.shanan.lufthansa.model.Schedule
import com.shanan.lufthansa.ui.map.MapActivity
import com.shanan.lufthansa.utils.Constants


/**
 * View Holder for a [Schedule] RecyclerView list item.
 */
class SchedulesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val departureTimeTv: TextView = view.findViewById(R.id.departureTime)
    private val departureAirportTv: TextView = view.findViewById(R.id.departureAirport)
    private val arrivalTimeTv: TextView = view.findViewById(R.id.arrivalTime)
    private val arrivalAirportTv: TextView = view.findViewById(R.id.arrivalAirport)
    private val totalTimeTv: TextView = view.findViewById(R.id.totalTime)
    private val stopsTv: TextView = view.findViewById(R.id.stops)

    private var schedule: Schedule? = null

    fun showScheduleData(schedule: Schedule) {

        this.schedule = schedule

        val airportsList: MutableList<String> = ArrayList()
        airportsList.add(schedule.flight.get(0).departure.airportCode)

        for (i in 0 until (schedule.flight.size)) {
            airportsList.add(schedule.flight.get(i).arrival.airportCode)
        }
        with(schedule.flight) {
            departureTimeTv.text = get(0).departure.scheduledTimeLocal.dateTime
                    .substring(get(0).departure.scheduledTimeLocal.dateTime.indexOf("T") + 1)
            departureAirportTv.text = get(0).departure.airportCode
        }

        totalTimeTv.text = schedule.totalJourney?.duration?.replace("P", "")?.replace("T", "")?.replace("D", "D ")?.replace("H", "H ")

        val stopsLength = airportsList.size - 2

        if (stopsLength > 0) {

            stopsTv.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            if (stopsLength == 1) {
                stopsTv.text = "$stopsLength stop"
            } else {
                stopsTv.text = "$stopsLength stops"
            }

            with(schedule.flight) {
                arrivalTimeTv.text = get(size - 1).arrival.scheduledTimeLocal.dateTime
                        .substring(get(size - 1).arrival.scheduledTimeLocal.dateTime.indexOf("T") + 1)
                arrivalAirportTv.text = get(size - 1).arrival.airportCode
            }

        } else {
            stopsTv.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            stopsTv.text = itemView.context.getString(R.string.direct)

            with(schedule.flight) {
                arrivalTimeTv.text = get(0).arrival.scheduledTimeLocal.dateTime
                        .substring(get(size - 1).arrival.scheduledTimeLocal.dateTime.indexOf("T") + 1)
                arrivalAirportTv.text = get(0).arrival.airportCode
            }
        }

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