package com.shanan.lufthansa.ui.airports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.airports.AirportRepository

/**
 * ViewModel for the [AirportsActivity] screen.
 * The ViewModel works with the [AirportRepository] to get the data.
 */
class AirportsViewModel(private val repository: AirportRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()

    /**
     * Get the last query value.
     */
    fun lastQueryValue(): String? = queryLiveData.value
}