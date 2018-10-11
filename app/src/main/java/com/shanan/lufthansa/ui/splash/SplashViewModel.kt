package com.shanan.lufthansa.ui.splash


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.model.AuthTokenResult

/**
 * ViewModel for the [SplashActivity] screen.
 * The ViewModel works with the [AirportRepository] to get the data.
 */
class SplashViewModel(val repository: AirportRepository) : ViewModel() {

    init {
        repository.authenticate()
    }

    var authResult: AuthTokenResult = repository.authTokenResult
    val loadingVisibility: MutableLiveData<Int> = repository.loadingVisibility
    val isAirportsCached: MutableLiveData<Boolean> = repository.isAirportsCached

    fun auth() {
        repository.authenticate()
    }

    fun getAirports(accessToken: String, isCached: Boolean) {
        repository.getAirports(accessToken, isCached)
    }

}