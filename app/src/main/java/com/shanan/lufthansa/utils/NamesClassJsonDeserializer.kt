package com.shanan.lufthansa.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.shanan.lufthansa.model.Name
import com.shanan.lufthansa.model.Names
import java.lang.reflect.Type


class NamesClassJsonDeserializer : JsonDeserializer<Names> {

    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): Names {

        val vals = ArrayList<Name>()

        val jsonObject = json.asJsonObject
        if (jsonObject != null) {

            val nameArray = jsonObject.get("Name")

            if (nameArray.isJsonArray) {
                for (i in 0 until nameArray.asJsonArray.size()) {
                    vals.add(ctx.deserialize<Any>(nameArray.asJsonArray[i], Name::class.java) as Name)
                }
            } else if (nameArray.isJsonObject) {
                vals.add(ctx.deserialize<Any>(nameArray.asJsonObject, Name::class.java) as Name)
            } else {
                throw RuntimeException("Unexpected JSON type: " + json.javaClass)
            }
        }

        return Names(vals)
    }
}