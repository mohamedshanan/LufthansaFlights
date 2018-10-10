package com.shanan.lufthansa.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.shanan.lufthansa.utils.NameListConverter

data class AirportsResponse(
        @SerializedName("AirportResource") val airportResource: AirportResource,
        val Meta: Meta
)

data class AirportResource(
        @SerializedName("Airports") val airports: Airports
)

data class Airports(
        @SerializedName("Airport") val airport: List<Airport>
)

@Entity
@TypeConverters(NameListConverter::class)
data class Airport(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @SerializedName("AirportCode") val airportCode: String,
        @Embedded @SerializedName("Position") val position: Position,
        @SerializedName("CityCode") val cityCode: String,
        @SerializedName("CountryCode") val countryCode: String,
        @SerializedName("LocationType") val locationType: String,
        @Embedded @SerializedName("Names") val names: Names
)

data class Names(
        @SerializedName("Name") val name: List<Name>
)

data class Name(
        @SerializedName("$") val value: String
)

data class Position(
        @SerializedName("Coordinate") val coordinate: Coordinate
)

data class Coordinate(
        @SerializedName("Latitude") val latitude: Double,
        @SerializedName("Longitude") val longitude: Double
)


data class Meta(
        @SerializedName("@Version") val version: String,
        @SerializedName("Link") val link: List<Link>,
        @SerializedName("TotalCount") val totalCount: Int
)

data class Link(
        @SerializedName("@Href") val href: String,
        @SerializedName("@Rel") val Rel: String
)