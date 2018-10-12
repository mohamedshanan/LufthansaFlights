package com.shanan.lufthansa.utils

import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import java.util.*

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

    fun showFutureDateDialog(context: Context,
                             onDateSetListener: DatePickerDialog.OnDateSetListener): DatePickerDialog {
        val calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(context, onDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))

        dpd.datePicker.minDate = calendar.time.time
        dpd.show()
        return dpd
    }

}