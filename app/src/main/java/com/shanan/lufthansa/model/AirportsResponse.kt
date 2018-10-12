package com.shanan.lufthansa.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.shanan.lufthansa.utils.NameListConverter

data class AirportsResponse(
        @SerializedName("AirportResource") val airportResource: AirportResource
)

data class AirportResource(
        @SerializedName("Airports") val airports: Airports,
        @SerializedName("Meta") val meta: Meta
)

data class Airports(
        @SerializedName("Airport") val airport: List<Airport>
)

@Entity
@TypeConverters(NameListConverter::class)
data class Airport(
        @PrimaryKey @SerializedName("AirportCode") val airportCode: String,
        @Embedded @SerializedName("Position") val position: Position,
        @SerializedName("CityCode") val cityCode: String,
        @SerializedName("CountryCode") val countryCode: String,
        @Embedded @SerializedName("Names") val names: Names
)

data class Names(
        @Embedded @SerializedName("Name") val name: Name
)

data class Name(
        @SerializedName("$") val value: String
)

data class Position(
        @Embedded @SerializedName("Coordinate") val coordinate: Coordinate
)

data class Coordinate(
        @SerializedName("Latitude") val latitude: Double?,
        @SerializedName("Longitude") val longitude: Double?
)


data class Meta(
        @SerializedName("TotalCount") val totalCount: Int
)