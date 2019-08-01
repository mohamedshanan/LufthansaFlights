package com.shanan.lufthansa.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.shanan.lufthansa.model.*
import java.lang.reflect.Type


class AirportJsonDeserializer : JsonDeserializer<Airport> {

    internal val gson = GsonBuilder()
            .registerTypeAdapter(Names::class.java, NamesClassJsonDeserializer())
            .registerTypeAdapter(ScheduleResource::class.java, ScheduleResourceJsonDeserializer())
            .registerTypeAdapter(Schedule::class.java, ScheduleJsonDeserializer())
            .registerTypeAdapter(Terminal::class.java, TerminalClassDeserializer())
            .setLenient()
            .create()


    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): Airport {

        var airportCode = ""
        var position = Position(Coordinate(0.0, 0.0))
        var cityCode = ""
        var countryCode = ""
        var names = Names(Name(""))

        val jsonObject = json.asJsonObject
        if (jsonObject != null) {

            val countryCodeJson = jsonObject.get("CountryCode")

            countryCode = if (countryCodeJson.isJsonObject) {
                ""
            } else {
                jsonObject.get("CountryCode").asString
            }

            airportCode = jsonObject.get("AirportCode").asString
            cityCode = jsonObject.get("CityCode").asString

            position = gson.fromJson(jsonObject.get("Position"), Position::class.java)
            names = gson.fromJson(jsonObject.get("Names"), Names::class.java)

//            position = jsonObject.get("Position") as Position
//            names = jsonObject.get("Names") as Names

        }

        return Airport(airportCode, position, cityCode, countryCode, names)
    }
}