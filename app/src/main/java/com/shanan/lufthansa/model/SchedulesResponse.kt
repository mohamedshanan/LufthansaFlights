package com.shanan.lufthansa.model

import com.google.gson.annotations.SerializedName


data class FlightsResponse(
        val ScheduleResource: ScheduleResource
)

data class ScheduleResource(
        @SerializedName("Schedule") val schedule: List<Schedule>
)

data class Schedule(
        @SerializedName("TotalJourney") val totalJourney: TotalJourney?,
        @SerializedName("Flight") val flight: List<Flight>
)

data class Flight(
        @SerializedName("Departure") val departure: Departure,
        @SerializedName("Arrival") val arrival: Arrival,
        @SerializedName("MarketingCarrier") val marketingCarrier: MarketingCarrier,
        @SerializedName("OperatingCarrier") val operatingCarrier: OperatingCarrier,
        @SerializedName("Equipment") val equipment: Equipment,
        @SerializedName("Details") val details: Details
)

data class OperatingCarrier(
        @SerializedName("AirlineID") val airlineID: String
)

data class Arrival(
        @SerializedName("AirportCode") val airportCode: String,
        @SerializedName("ScheduledTimeLocal") val scheduledTimeLocal: ScheduledTimeLocal,
        @SerializedName("Terminal") val terminal: Terminal
)

data class ScheduledTimeLocal(
        @SerializedName("DateTime") val dateTime: String
)

data class Terminal(
        @SerializedName("Name") val name: String
)

data class Equipment(
        @SerializedName("AircraftCode") val aircraftCode: String
)

data class MarketingCarrier(
        @SerializedName("AirlineID") val airlineID: String,
        @SerializedName("FlightNumber") val flightNumber: Int
)

data class Departure(
        @SerializedName("AirportCode") val airportCode: String,
        @SerializedName("ScheduledTimeLocal") val scheduledTimeLocal: ScheduledTimeLocal,
        @SerializedName("Terminal") val terminal: Terminal
)

data class Details(
        @SerializedName("Stops") val stops: Stops,
        @SerializedName("DaysOfOperation") val daysOfOperation: Int,
        @SerializedName("DatePeriod") val datePeriod: DatePeriod
)

data class DatePeriod(
        @SerializedName("Effective") val effective: String,
        @SerializedName("Expiration") val expiration: String
)

data class Stops(
        @SerializedName("StopQuantity") val stopQuantity: Int
)

data class TotalJourney(
        @SerializedName("Duration") val duration: String
)