package com.shanan.lufthansa.data.airports

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.shanan.lufthansa.api.LufthansaService
import com.shanan.lufthansa.api.getAirports
import com.shanan.lufthansa.api.requestAccessToken
import com.shanan.lufthansa.data.airports.db.AirportLocalCache
import com.shanan.lufthansa.model.AuthResponse
import com.shanan.lufthansa.model.AuthTokenResult
import com.shanan.lufthansa.utils.Constants

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

            if (it.isEmpty()) {
                requestAccessToken(service, { auth ->
                    cache.insert(auth) {
                        isRequestInProgress = false
                        getAirports(it[0].access_token)
                    }

                }, { error ->
                    isRequestInProgress = false
                    authTokenResult.networkErrors.postValue(error)
                    loadingVisibility.value = View.GONE

                })
            } else {
                isRequestInProgress = false
                getAirports(it[0].access_token)
            }
        }
    }

    /**
     * Search airports whose names match the query.
     */
    fun getAirports(token: String) {

        if (isRequestInProgress) return

        isRequestInProgress = true

        getAirports(service, token, offset, LIMIT, Constants.DEFAULT_LANG, { airports, totalCount ->
            cache.insert(airports) {
                offset += LIMIT
                isRequestInProgress = false

                // Request next batch of airports
                if (offset < totalCount) {
                    getAirports(token)
                }

                Log.d(TAG, "offset ${offset}")

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