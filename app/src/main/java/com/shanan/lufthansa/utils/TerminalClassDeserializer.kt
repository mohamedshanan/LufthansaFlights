package com.shanan.lufthansa.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.shanan.lufthansa.model.Terminal
import java.lang.reflect.Type

class TerminalClassDeserializer : JsonDeserializer<Terminal> {

    @Throws(JsonParseException::class)
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Terminal {

        val jsonObject = element.asJsonObject
        if (jsonObject != null) {
            val nameObject = jsonObject.get("Name")
            return Terminal(nameObject.toString())
        } else {
            return Terminal("")
        }

    }

}
