package com.shanan.lufthansa.data.airports.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shanan.lufthansa.model.AuthResponse

/**
 * Room data access object for accessing the [Auth] table.
 */
@Dao
interface AuthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(auth: AuthResponse): Long

    // get saved token if it is not expired
    @Query("SELECT * FROM Auth WHERE expiresAt > :currentTime LIMIT 1")
    fun getAccessToken(currentTime: Long): LiveData<List<AuthResponse>>

}