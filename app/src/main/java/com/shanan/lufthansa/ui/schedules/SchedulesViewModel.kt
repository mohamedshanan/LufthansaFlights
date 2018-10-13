package com.shanan.lufthansa.ui.schedules

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.flights.SchedulesRepository
import com.shanan.lufthansa.model.FlightsResponse
import com.shanan.lufthansa.model.ScheduleRequest

/**
 * ViewModel for the [SchedulesActivity] screen.
 * The ViewModel works with the [SchedulesRepository] to get the data.
 */
class SchedulesViewModel(private val repository: SchedulesRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 10
    }

    val loadingVisibility = MutableLiveData<Int>()
    private val queryLiveData = MutableLiveData<ScheduleRequest>()

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
    fun searchRepo(request: ScheduleRequest) {
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

    private fun lastQueryValue(): ScheduleRequest? = queryLiveData.value
}