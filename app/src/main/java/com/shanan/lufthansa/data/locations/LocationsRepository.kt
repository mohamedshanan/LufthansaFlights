package com.shanan.lufthansa.data.locations

import androidx.lifecycle.MutableLiveData
import com.shanan.lufthansa.data.airports.db.AirportLocalCache
import com.shanan.lufthansa.model.Airport

class LocationsRepository(private val cache: AirportLocalCache) {

    private val TAG = "LocationsRepository"
    var searchResults = MutableLiveData<List<Airport>>()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    fun getAirportsListByCodes(query: List<String>) {
        if (isRequestInProgress) return
        isRequestInProgress = true
        cache.getAirportsListByCodes(query).observeForever {
            var mutableResults = it.toMutableList()
            for (i in 0 until it.size) {
                val index = query.indexOf(it[i].airportCode)
                mutableResults.set(index, it[i])
            }

            searchResults.postValue(mutableResults)
            isRequestInProgress = false
        }
    }
}