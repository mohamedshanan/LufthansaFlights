package com.shanan.lufthansa.injection

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.shanan.lufthansa.api.LufthansaService
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.data.airports.db.AirportDatabase
import com.shanan.lufthansa.data.airports.db.AirportLocalCache
import java.util.concurrent.Executors


/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
object Injection {

    /**
     * Creates an instance of [AirportLocalCache] based on the database DAO.
     */
    fun provideCache(context: Context): AirportLocalCache {
        val database = AirportDatabase.getInstance(context)
        return AirportLocalCache(database.airportDao(), database.authDao(), Executors.newSingleThreadExecutor())
    }

    /**
     * Creates an instance of [AirportRepository] based on the [LufthansaService] and a
     * [AirportLocalCache]
     */
    private fun provideAirportRepository(context: Context): AirportRepository {
        return AirportRepository(LufthansaService.create(), provideCache(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideAirportRepository(context))
    }

}