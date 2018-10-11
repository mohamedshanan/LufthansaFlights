package com.shanan.lufthansa.data.airports

import android.util.Log
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
    // keep the last requested item index. When the request is successful, increment the page number.
    private var offset = 0

    // LiveData of data and network errors.
    val networkErrors = MutableLiveData<String>()
    val authResponse = MutableLiveData<AuthResponse>()
    var authTokenResult = AuthTokenResult(authResponse, networkErrors)

    var isAirportsCached: MutableLiveData<Boolean> = MutableLiveData()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    fun authenticate() {

        if (isRequestInProgress) return

        isRequestInProgress = true

        cache.getAccessToken().observeForever {

            if (it.isEmpty()) {
                requestAccessToken(service, { auth ->
                    cache.insert(auth) {
                        isRequestInProgress = false
                        authResponse.postValue(auth)
                    }

                }, { error ->
                    isRequestInProgress = false
                    authTokenResult.networkErrors.postValue(error)

                })
            } else {
                isRequestInProgress = false
                authResponse.postValue(it[0])
            }
        }
    }

    fun getAirports(token: String, isCached: Boolean) {

        if (isCached) {
            isAirportsCached.postValue(true)
        } else {
            getAirports(token)
        }
    }

    /**
     * Getting all airports paginated and saving them to search them later
     */
    fun getAirports(token: String) {

        if (isRequestInProgress) return
        isRequestInProgress = true

        getAirports(service, token, offset, LIMIT, Constants.DEFAULT_LANG, { airports, totalCount ->
            cache.insert(airports) {
                offset += LIMIT
                isRequestInProgress = false

                Log.d(TAG, "offset ${offset}")

                // Request next batch of airports
                if (offset < totalCount) {
                    getAirports(token)
                } else {
                    isAirportsCached.postValue(true)
                    isRequestInProgress = false
                }
            }
        }, { error ->
            networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }

    companion object {
        private const val LIMIT = 100
    }
}