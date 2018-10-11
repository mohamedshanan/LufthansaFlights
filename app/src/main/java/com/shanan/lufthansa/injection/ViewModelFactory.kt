package com.shanan.lufthansa.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.ui.airports.AirportsViewModel
import com.shanan.lufthansa.ui.splash.SplashViewModel

/**
 * Factory for ViewModels
 */
class ViewModelFactory(private val repository: AirportRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AirportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AirportsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}