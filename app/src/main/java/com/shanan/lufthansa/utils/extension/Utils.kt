package com.shanan.lufthansa.utils.extension

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager

object Utils {

    fun saveBooleanToPrefs(key: String, value: Boolean, context: Context) {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        defaultSharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBooleanFromPrefs(key: String, context: Context): Boolean {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return defaultSharedPreferences.getBoolean(key, false)
    }

    // Checking Network State
    fun getNetworkState(ctx: Context): Boolean {

        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork.isConnected
    }

}