package com.shanan.lufthansa.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.data.flights.FlightRepository
import com.shanan.lufthansa.ui.flights.FlightsViewModel
import com.shanan.lufthansa.ui.landing.LandingViewModel

/**
 * Factory for ViewModels
 */
class ViewModelFactory<R>(private val repository: R) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightsViewModel(repository as FlightRepository) as T
        }
        if (modelClass.isAssignableFrom(LandingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LandingViewModel(repository as AirportRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}