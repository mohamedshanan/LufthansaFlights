package com.shanan.lufthansa.ui.flights

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.flights.FlightRepository
import com.shanan.lufthansa.model.FlightRequest
import com.shanan.lufthansa.model.FlightsResponse

/**
 * ViewModel for the [FlightsActivity] screen.
 * The ViewModel works with the [FlightRepository] to get the data.
 */
class FlightsViewModel(private val repository: FlightRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 10
    }

    val loadingVisibility = MutableLiveData<Int>()
    private val queryLiveData = MutableLiveData<FlightRequest>()

    var flightsResult: MutableLiveData<FlightsResponse> = repository.flightsResponse
    var error: MutableLiveData<String> = repository.networkErrors

    init {
        flightsResult.observeForever {
            loadingVisibility.value = View.GONE
        }
        error.observeForever {
            loadingVisibility.value = View.GONE
        }
    }

    /**
     * Search a repository based on a query string.
     */
    fun searchRepo(request: FlightRequest) {
        loadingVisibility.value = View.VISIBLE
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