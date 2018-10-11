package com.shanan.lufthansa.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Auth")
data class AuthResponse(@PrimaryKey(autoGenerate = true) val id: Long, val access_token: String, val token_type: String, val expires_in: Long, var expiresAt: Long)