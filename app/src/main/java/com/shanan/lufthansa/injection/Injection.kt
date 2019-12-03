package com.shanan.lufthansa.injection

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shanan.lufthansa.api.LufthansaService
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.data.airports.db.AirportDatabase
import com.shanan.lufthansa.data.airports.db.AirportLocalCache
import com.shanan.lufthansa.data.flights.SchedulesRepository
import com.shanan.lufthansa.data.locations.LocationsRepository
import com.shanan.lufthansa.model.*
import com.shanan.lufthansa.ui.landing.LandingViewModel
import com.shanan.lufthansa.ui.map.MapViewModel
import com.shanan.lufthansa.ui.schedules.SchedulesViewModel
import com.shanan.lufthansa.utils.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
class Injection {

    /**
     * Creates an instance of [AirportLocalCache] based on the database DAO.
     */

    fun provideGson() : Gson {
        return GsonBuilder()
                .registerTypeAdapter(Airport::class.java, AirportJsonDeserializer())
                .registerTypeAdapter(Names::class.java, NamesClassJsonDeserializer())
                .registerTypeAdapter(ScheduleResource::class.java, ScheduleResourceJsonDeserializer())
                .registerTypeAdapter(Schedule::class.java, ScheduleJsonDeserializer())
                .registerTypeAdapter(Terminal::class.java, TerminalClassDeserializer())
                .setLenient()
                .create()
    }

    private fun provideRetrofit() : Retrofit {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

        return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .build()
    }

    fun provideCache(context: Context): AirportLocalCache {
        val database = AirportDatabase.getInstance(context)
        return AirportLocalCache(database.airportDao(), database.authDao(), Executors.newSingleThreadExecutor())
    }

    /**
     * Creates an instance of [SchedulesRepository] based on the [LufthansaService] and a
     * [AirportLocalCache]
     */
    private fun provideFlightRepository(context: Context): SchedulesRepository {
        return SchedulesRepository(provideRetrofit().create(LufthansaService::class.java), provideCache(context))
    }

    /**
     * Creates an instance of [AirportRepository] based on the [LufthansaService] and a
     * [AirportLocalCache]
     */
    private fun provideAirportRepository(context: Context): AirportRepository {
        return AirportRepository(provideRetrofit().create(LufthansaService::class.java), provideCache(context))
    }

    /**
     * Creates an instance of [LocationsRepository] based on the [AirportLocalCache]
     */

    private fun provideLocationsRepository(context: Context): LocationsRepository {
        return LocationsRepository(provideCache(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */

    fun <T : ViewModel> provideViewModelFactory(context: Context, modelClass: Class<T>): ViewModelProvider.Factory {
        if (modelClass.isAssignableFrom(SchedulesViewModel::class.java)) {
            return ViewModelFactory(provideFlightRepository(context))
        }
        if (modelClass.isAssignableFrom(LandingViewModel::class.java)) {
            return ViewModelFactory(provideAirportRepository(context))
        }
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return ViewModelFactory(provideLocationsRepository(context))
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}