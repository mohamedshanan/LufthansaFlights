package com.shanan.lufthansa.ui.schedules

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shanan.lufthansa.R
import com.shanan.lufthansa.model.Schedule
import com.shanan.lufthansa.ui.map.MapActivity
import com.shanan.lufthansa.utils.extension.toast


/**
 * View Holder for a [Schedule] RecyclerView list item.
 */
class SchedulesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val stopsTv: TextView = view.findViewById(R.id.stops)
    private val operator: TextView = view.findViewById(R.id.flightNumber)
    private val totalTime: TextView = view.findViewById(R.id.totalTime)

    private var schedule: Schedule? = null

    init {
        view.setOnClickListener {
            schedule?.flight?.get(0)?.marketingCarrier?.flightNumber.let { flightNumber ->
                Log.d("Flight", flightNumber.toString())
                view.context.toast(flightNumber.toString())
                view.context.startActivity(Intent(view.context, MapActivity::class.java))
            }
        }
    }

    fun showScheduleData(schedule: Schedule) {

        this.schedule = schedule
        operator.text = schedule.flight.get(0).marketingCarrier.airlineID

        val stops = StringBuilder()
        stops.append(schedule.flight.get(0).departure.airportCode)
        for (i in 0..(schedule.flight.size.minus(1))) {
            stops.append(" - ")
            stops.append(schedule.flight.get(i).arrival.airportCode)
        }
        stopsTv.text = stops.toString()

        totalTime.text = schedule.totalJourney?.duration?.replace("PT", "")?.replace("D", "D ")?.replace("H", "H ")

    }

    companion object {
        fun create(parent: ViewGroup): SchedulesViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_flight, parent, false)
            return SchedulesViewHolder(view)
        }
    }
}