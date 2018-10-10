package com.shanan.lufthansa.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shanan.lufthansa.model.Airport

/**
 * Room data access object for accessing the [Airport] table.
 */
@Dao
interface AirportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<Airport>)

    // Do a similar query as the search API:
    // Look for airports that contain the query string in the name
    @Query("SELECT * FROM Airport WHERE (cityCode LIKE :queryString) OR (airportCode LIKE " +
            ":queryString) ")
    fun airportByName(queryString: String): LiveData<List<Airport>>

}