package com.shanan.lufthansa.data.flights

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.shanan.lufthansa.api.LufthansaService
import com.shanan.lufthansa.api.searchFlights
import com.shanan.lufthansa.data.airports.db.AirportLocalCache
import com.shanan.lufthansa.model.FlightRequest
import com.shanan.lufthansa.model.FlightsResponse

class FlightRepository(private val service: LufthansaService, private val cache: AirportLocalCache) {

    // keep the last requested page. When the request is successful, increment the page number.
    private var offset = 0

    // LiveData of data and network errors.
    val networkErrors = MutableLiveData<String>()
    val flightsResponse = MutableLiveData<FlightsResponse>()
    var accessToken: String? = null

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    /**
     * Search flight with origin, destination and date
     */
    fun search(request: FlightRequest) {
        Log.d("FlightRepository", "New query: $request")
        offset = 0

        cache.getAccessToken().observeForever {
            if (!it.isEmpty()) {
                accessToken = it[0].access_token
                requestData(request)
            }
        }
    }

    fun requestMore(request: FlightRequest) {
        requestData(request)
    }

    private fun requestData(query: FlightRequest) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        accessToken?.let {
            searchFlights(service, accessToken!!, query, offset, LIMIT, {
                flightsResponse.postValue(it)
                offset += offset
                isRequestInProgress = false
            }, { error ->
                networkErrors.postValue(error)
                isRequestInProgress = false
            })
        }
    }

    companion object {
        private const val LIMIT = 10
    }
}