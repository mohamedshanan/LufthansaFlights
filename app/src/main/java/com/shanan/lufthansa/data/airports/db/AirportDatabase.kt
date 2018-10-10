package com.shanan.lufthansa.data.airports.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.utils.Constants.AIRPORT_DB_NAME

/**
 * Database schema that holds the list of airports.
 */
@Database(
        entities = [Airport::class],
        version = 1,
        exportSchema = false
)
abstract class AirportDatabase : RoomDatabase() {

    abstract fun airportDao(): AirportDao

    companion object {

        @Volatile
        private var INSTANCE: AirportDatabase? = null

        fun getInstance(context: Context): AirportDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AirportDatabase::class.java, AIRPORT_DB_NAME)
                        .build()
    }
}