package com.shanan.lufthansa.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.data.flights.SchedulesRepository
import com.shanan.lufthansa.data.locations.LocationsRepository
import com.shanan.lufthansa.ui.landing.LandingViewModel
import com.shanan.lufthansa.ui.map.MapViewModel
import com.shanan.lufthansa.ui.schedules.SchedulesViewModel

/**
 * Factory for ViewModels
 */
class ViewModelFactory<R>(private val repository: R) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchedulesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SchedulesViewModel(repository as SchedulesRepository) as T
        }
        if (modelClass.isAssignableFrom(LandingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LandingViewModel(repository as AirportRepository) as T
        }
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository as LocationsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}