package com.shanan.lufthansa.ui.airports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.model.AirportSearchResult

/**
 * ViewModel for the [AirportsActivity] screen.
 * The ViewModel works with the [AirportRepository] to get the data.
 */
class AirportsViewModel(private val repository: AirportRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()
    private val repoResult: LiveData<AirportSearchResult> = Transformations.map(queryLiveData, {
        repository.search(it)
    })

    val airport: LiveData<List<Airport>> = Transformations.switchMap(repoResult,
            { it -> it.data })
    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult,
            { it -> it.networkErrors })

    /**
     * Search an airport based on a query string.
     */
    fun searchRepo(queryString: String) {
        queryLiveData.postValue(queryString)
        repository.search(queryString)
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
    fun lastQueryValue(): String? = queryLiveData.value
}