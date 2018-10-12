package com.shanan.lufthansa.ui.splash


import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.model.AuthTokenResult

/**
 * ViewModel for the [SplashActivity] screen.
 * The ViewModel works with the [AirportRepository] to get the data.
 */
class SplashViewModel(val repository: AirportRepository) : ViewModel() {

    var authResult: AuthTokenResult = repository.authTokenResult
    val loadingVisibility = MutableLiveData<Int>()
    val searchVisibility = MutableLiveData<Int>()
    val isAirportsCached: MutableLiveData<Boolean> = repository.isAirportsCached
    val searchResults: MutableLiveData<List<Airport>> = repository.searchResults

    init {
        loadingVisibility.value = View.VISIBLE
        searchVisibility.value = View.GONE

        repository.authenticate()

        authResult.data.observeForever {
            loadingVisibility.value = View.GONE
        }
        authResult.networkErrors.observeForever {
            loadingVisibility.value = View.GONE
        }

        isAirportsCached.observeForever {
            loadingVisibility.value = View.GONE
            searchVisibility.value = View.VISIBLE
        }
    }

    fun auth() {
        repository.authenticate()
    }

    fun getAirports(accessToken: String, isCached: Boolean) {
        loadingVisibility.value = View.VISIBLE
        searchVisibility.value = View.GONE
        repository.getAirports(accessToken, isCached)
    }

    fun search(query: CharSequence) {
        repository.searchAirports(query.toString())
    }

    fun getFlights(originCode: String?, destinationCode: String?) {
        Log.d("_SplashActivity_", "from : ${originCode} to  : ${destinationCode}")


    }
}