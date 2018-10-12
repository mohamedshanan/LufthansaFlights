package com.shanan.lufthansa.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.shanan.lufthansa.model.Flight
import com.shanan.lufthansa.model.Schedule
import com.shanan.lufthansa.model.TotalJourney
import java.lang.reflect.Type


class ScheduleJsonDeserializer : JsonDeserializer<Schedule> {

    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): Schedule {

        val flightList: MutableList<Flight> = ArrayList()
        var totalJourney: TotalJourney? = null

        val jsonObject = json.asJsonObject
        if (jsonObject != null) {

            val flightJson = jsonObject.get("Flight")

            val totalJourneyJson = jsonObject.get("TotalJourney")
            totalJourney = ctx.deserialize<Any>(
                    totalJourneyJson.asJsonObject, TotalJourney::class.java) as TotalJourney

            if (flightJson.isJsonArray) {
                for (i in 0..(flightJson.asJsonArray.size() - 1)) {
                    val scheduleItem = flightJson.asJsonArray.get(i).asJsonObject
                    flightList.add(ctx.deserialize<Any>(scheduleItem, Flight::class.java) as Flight)
                }

            } else if (flightJson.isJsonObject) {
                flightList.add(ctx.deserialize<Any>(flightJson.asJsonObject, Flight::class.java) as Flight)
            } else {
                throw RuntimeException("Unexpected JSON type: " + json.javaClass)
            }
        }

        return Schedule(totalJourney, flightList)
    }
}