package com.shanan.lufthansa.ui.schedules

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shanan.lufthansa.model.Schedule

/**
 * Adapter for the list of flights.
 */
class SchedulesAdapter : ListAdapter<Schedule, RecyclerView.ViewHolder>(SCHEDULE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SchedulesViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val schedule = getItem(position)
        if (schedule != null) {
            with(holder as SchedulesViewHolder) {
                showScheduleData(schedule)
            }
        }
    }

    companion object {
        private val SCHEDULE_COMPARATOR = object : DiffUtil.ItemCallback<Schedule>() {
            override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean =
                    getStopsString(oldItem).equals(getStopsString(newItem)) ||
                            oldItem.flight.get(0).marketingCarrier.flightNumber ==
                            newItem.flight.get(0).marketingCarrier.flightNumber

            override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean =
                    oldItem == newItem
        }

        fun getStopsString(schedule: Schedule): String {

            val stops = StringBuilder()

            stops.append(schedule.flight[0].departure.airportCode)

            for (i in 0..(schedule.flight.size.minus(1))) {
                stops.append(" >> ")
                stops.append(schedule.flight[i].arrival.airportCode)
            }
            return stops.toString()
        }
    }
}