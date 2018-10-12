package com.shanan.lufthansa.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.shanan.lufthansa.model.Name
import com.shanan.lufthansa.model.Names
import java.lang.reflect.Type


class NamesClassJsonDeserializer : JsonDeserializer<Names> {

    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): Names {

        var name: Name

        val jsonObject = json.asJsonObject
        if (jsonObject != null) {

            val nameObject = jsonObject.get("Name")

            if (nameObject.isJsonArray) {
                name = ctx.deserialize<Any>(nameObject.asJsonArray[0], Name::class.java) as Name

            } else if (nameObject.isJsonObject) {
                name = ctx.deserialize<Any>(nameObject.asJsonObject, Name::class.java) as Name
            } else {
                throw RuntimeException("Unexpected JSON type: " + json.javaClass)
            }
        } else {
            name = Name("")
        }

        return Names(name)
    }
}