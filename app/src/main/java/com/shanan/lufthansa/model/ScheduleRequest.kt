package com.shanan.lufthansa.model

import java.io.Serializable

data class ScheduleRequest(
        var origin: String,
        var destination: String,
        var fromDateTime: String
) : Serializable