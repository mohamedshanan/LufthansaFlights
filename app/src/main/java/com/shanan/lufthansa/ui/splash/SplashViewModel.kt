package com.shanan.lufthansa.ui.splash

import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.airports.AirportRepository

/**
 * ViewModel for the [SplashActivity] screen.
 * The ViewModel works with the [AirportRepository] to get the data.
 */
class AuthViewModel(private val repository: AirportRepository) : ViewModel() {

    fun auth() {
        repository.auth()
    }
}