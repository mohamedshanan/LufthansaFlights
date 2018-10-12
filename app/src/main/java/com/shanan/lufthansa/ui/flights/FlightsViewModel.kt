package com.shanan.lufthansa.ui.flights

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.data.flights.FlightRepository
import com.shanan.lufthansa.model.FlightRequest
import com.shanan.lufthansa.model.FlightsResponse

/**
 * ViewModel for the [FlightsActivity] screen.
 * The ViewModel works with the [AirportRepository] to get the data.
 */
class FlightsViewModel(private val repository: FlightRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<FlightRequest>()

    var flightsResult: MutableLiveData<FlightsResponse> = repository.flightsResponse
    var error: MutableLiveData<String> = repository.networkErrors

    /**
     * Search a repository based on a query string.
     */
    fun searchRepo(request: FlightRequest) {
        queryLiveData.postValue(request)
        repository.search(request)
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            val immutableQuery = lastQueryValue()
            if (immutableQuery != null) {
                repository.requestMore(immutableQuery)
            }
        }
    }

    /**
     * Get the last query value.
     */

    private fun lastQueryValue(): FlightRequest? = queryLiveData.value
}