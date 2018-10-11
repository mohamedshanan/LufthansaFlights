package com.shanan.lufthansa.data.airports

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.shanan.lufthansa.api.LufthansaService
import com.shanan.lufthansa.api.getAirports
import com.shanan.lufthansa.api.requestAccessToken
import com.shanan.lufthansa.data.airports.db.AirportLocalCache
import com.shanan.lufthansa.model.AirportSearchResult
import com.shanan.lufthansa.model.AuthResponse
import com.shanan.lufthansa.model.AuthTokenResult

/**
 * Repository class that works with local and remote data sources.
 */
class AirportRepository(
        private val service: LufthansaService,
        private val cache: AirportLocalCache
) {

    private val TAG = "AirportRepository"
    // keep the last requested page. When the request is successful, increment the page number.
    private var offset = 0

    // LiveData of data and errors.
    val networkErrors = MutableLiveData<String>()
    val authResponse = MutableLiveData<AuthResponse>()
    var authTokenResult = AuthTokenResult(authResponse, networkErrors)
    var loadingVisibility: MutableLiveData<Int> = MutableLiveData()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    fun authenticate() {

        if (isRequestInProgress) return

        isRequestInProgress = true
        loadingVisibility.value = View.VISIBLE

        cache.getAccessToken().observeForever {
            Log.d(TAG, "token : ${it?.size}")

            if (it.isEmpty()) {
                requestAccessToken(service, { auth ->
                    cache.insert(auth) {
                        authTokenResult.data.postValue(auth)
                        isRequestInProgress = false
                        loadingVisibility.value = View.GONE
                    }

                }, { error ->
                    authTokenResult.networkErrors.postValue(error)
                    isRequestInProgress = false
                    loadingVisibility.value = View.GONE

                })
            } else {
                authTokenResult.data.postValue(it[0])
                loadingVisibility.value = View.GONE
                isRequestInProgress = false

            }
        }
    }

    /**
     * Search airports whose names match the query.
     */
    fun search(query: String): AirportSearchResult {
        Log.d(TAG, "New query: $query")
        offset = 0
        requestAndSaveData(query)

        // Get data from the local cache
        val data = cache.airportByName(query)

        return AirportSearchResult(data, networkErrors)
    }

    fun requestMore(query: String) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        loadingVisibility.value = View.VISIBLE

        getAirports(service, query, offset, LIMIT, { airports ->
            cache.insert(airports) {
                offset += LIMIT
                isRequestInProgress = false
                loadingVisibility.value = View.GONE

            }
        }, { error ->
            networkErrors.postValue(error)
            isRequestInProgress = false
            loadingVisibility.value = View.GONE

        })
    }

    companion object {
        private const val LIMIT = 100
    }
}