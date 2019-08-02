package com.shanan.lufthansa.ui.landing


import android.app.DatePickerDialog
import android.view.View
import android.widget.DatePicker
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.airports.AirportRepository
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.model.AuthResponse
import com.shanan.lufthansa.model.RequestResult
import com.shanan.lufthansa.model.ScheduleRequest
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for the [LandingActivity] screen.
 * The ViewModel works with the [AirportRepository] to get the data.
 */
class LandingViewModel(val repository: AirportRepository) : ViewModel(), DatePickerDialog.OnDateSetListener {

    val departureDate: ObservableField<String> = ObservableField()

    var authResult: RequestResult<AuthResponse> = repository.requestResult
    val loadingVisibility = MutableLiveData<Int>()
    val searchVisibility = MutableLiveData<Int>()
    val isAirportsCached: MutableLiveData<Boolean> = repository.isAirportsCached
    val searchResults: MutableLiveData<List<Airport>> = repository.searchResults
    val flightRequest: MutableLiveData<ScheduleRequest> = MutableLiveData()

    init {
        loadingVisibility.value = View.VISIBLE
        searchVisibility.value = View.GONE

        repository.authenticate()

        authResult.data.observeForever {
            loadingVisibility.value = View.GONE
        }
        authResult.error.observeForever {
            loadingVisibility.value = View.GONE
        }

        isAirportsCached.observeForever {
            loadingVisibility.value = View.GONE
            searchVisibility.value = View.VISIBLE
        }
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

        if (originCode != null && destinationCode != null && departureDate.get() != null) {
            if (!originCode.equals(destinationCode)) {
                flightRequest.postValue(ScheduleRequest(originCode, destinationCode, departureDate.get().toString()))
            } else {
                authResult.error.postValue("Origin and destination airports must be different")
            }
        } else {
            authResult.error.postValue("Please select origin, destination and departure date then try again.")
        }
    }

    fun setDate() {
        val c = Calendar.getInstance()

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val formattedDate = df.format(c.time)

        departureDate.set(formattedDate)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val monthStr = if (month + 1 < 10) "0".plus(month + 1) else (month + 1).toString()
        val dayStr = if (dayOfMonth < 10) "0".plus(month + 1) else (dayOfMonth).toString()
        val formattedDate = "$year-$monthStr-$dayStr"
        departureDate.set(formattedDate)
    }

    fun authenticate() {
        repository.authenticate()
        loadingVisibility.value = View.VISIBLE
        searchVisibility.value = View.GONE
    }

}