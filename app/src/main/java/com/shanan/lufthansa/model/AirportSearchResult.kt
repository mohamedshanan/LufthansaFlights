package com.shanan.lufthansa.model

import androidx.lifecycle.LiveData


/**
 * AirportSearchResult from a search, which contains LiveData<List<Airport>> holding query data,
 * and a LiveData<String> of network error state.
 */
data class AirportSearchResult(
        val data: LiveData<List<Airport>>,
        val networkErrors: LiveData<String>
)