package com.shanan.lufthansa.ui.landing


import android.app.DatePickerDialog
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.model.AuthTokenResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for the [LandingActivity] screen.
 * The ViewModel works with the [AirportRepository] to get the data.
 */
class LandingViewModel(val repository: AirportRepository) : ViewModel(), DatePickerDialog.OnDateSetListener {

    val departureDate: ObservableField<String> = ObservableField()

    var authResult: AuthTokenResult = repository.authTokenResult
    val loadingVisibility = MutableLiveData<Int>()
    val searchVisibility = MutableLiveData<Int>()
    val isAirportsCached: MutableLiveData<Boolean> = repository.isAirportsCached
    val searchResults: MutableLiveData<List<Airport>> = repository.searchResults

    init {
        loadingVisibility.value = View.VISIBLE
        searchVisibility.value = View.GONE

        repository.authenticate()

        authResult.data.observeForever {
            loadingVisibility.value = View.GONE
        }
        authResult.networkErrors.observeForever {
            loadingVisibility.value = View.GONE
        }

        isAirportsCached.observeForever {
            loadingVisibility.value = View.GONE
            searchVisibility.value = View.VISIBLE
        }
    }

    fun auth() {
        repository.authenticate()
    }

    fun getAirports(accessToken: String, isCached: Boolean) {
        loadingVisibility.value = View.VISIBLE
        searchVisibility.value = View.GONE
        repository.getAirports(accessToken, isCached)
    }

    fun search(query: CharSequence) {
        repository.searchAirports(query.toString())
    }

    fun getFlights(originCode: String?, destinationCode: String?) {
        Log.d("_SplashActivity_", "from : ${originCode} to  : ${destinationCode} on ${departureDate.get()}")
    }

    fun setDate() {
        val c = Calendar.getInstance()
        System.out.println("Current time => " + c.time)

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val formattedDate = df.format(c.time)

        departureDate.set(formattedDate)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val monthStr: String
        if (month + 1 < 10) {
            monthStr = "0".plus(month + 1)
        } else {
            monthStr = (month + 1).toString()
        }
        val formattedDate = "${year}-${monthStr}-${dayOfMonth}"
        departureDate.set(formattedDate)
    }

}