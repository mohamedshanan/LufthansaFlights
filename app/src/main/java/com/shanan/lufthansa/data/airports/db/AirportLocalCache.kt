package com.shanan.lufthansa.data.airports.db

import android.util.Log
import androidx.lifecycle.LiveData
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.model.AuthResponse
import java.util.concurrent.Executor

/**
 * Class that handles the DAO local data source. This ensures that methods are triggered on the
 * correct executor.
 */
class AirportLocalCache(
        private val airportDao: AirportDao,
        private var authDao: AuthDao,
        private val ioExecutor: Executor
) {

    /**
     * Insert a list of airports in the database, on a background thread.
     */
    fun insert(airports: List<Airport>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Log.d("AirportLocalCache", "inserting ${airports.size} airports")
            airportDao.insert(airports)
            insertFinished()
        }
    }

    /**
     * Request a LiveData<List<Airport>> from the Dao, based on a airport name. If the name contains
     * multiple words separated by spaces, then we allow any characters between the words.
     * @param name airport name
     */
    fun searchAirports(query: String): LiveData<List<Airport>> {
        // appending '%' so we can allow other characters to be before and after the query string
        val query = "%${query.toUpperCase().trim()}%"
        return airportDao.searchAirports(query)
    }

    /**
     * Insert AuthResponse the database, on a background thread.
     */
    fun insert(authResponse: AuthResponse?, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Log.d("insertToken", "inserting ${authResponse?.access_token}")
            authResponse?.let {
                Log.d("AirportLocalCache", "inserting ${authResponse.access_token}")

                it.expiresAt = System.currentTimeMillis() + (it.expires_in.times(1000))
                val addedId = authDao.insert(it)
                Log.d("insertToken", "addedId $addedId")
                insertFinished()
            }

        }
    }

    /**
     * request access token
     */
    fun getAccessToken(): LiveData<List<AuthResponse>> {
        return authDao.getAccessToken(System.currentTimeMillis())
    }
}