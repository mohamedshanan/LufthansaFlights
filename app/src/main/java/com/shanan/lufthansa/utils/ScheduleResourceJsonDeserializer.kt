package com.shanan.lufthansa.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.shanan.lufthansa.model.Schedule
import com.shanan.lufthansa.model.ScheduleResource
import java.lang.reflect.Type


class ScheduleResourceJsonDeserializer : JsonDeserializer<ScheduleResource> {

    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): ScheduleResource {

        val schedulesList: MutableList<Schedule> = ArrayList()

        val jsonObject = json.asJsonObject
        if (jsonObject != null) {

            val schedule = jsonObject.get("Schedule")

            if (schedule.isJsonArray) {
                for (i in 0..(schedule.asJsonArray.size() - 1)) {
                    val scheduleItem = schedule.asJsonArray.get(i).asJsonObject
                    schedulesList.add(ctx.deserialize<Any>(scheduleItem, Schedule::class.java) as Schedule)
                }

            } else if (schedule.isJsonObject) {
                schedulesList.add(ctx.deserialize<Any>(schedule.asJsonObject, Schedule::class.java) as Schedule)
            } else {
                throw RuntimeException("Unexpected JSON type: " + json.javaClass)
            }
        }

        return ScheduleResource(schedulesList)
    }
}