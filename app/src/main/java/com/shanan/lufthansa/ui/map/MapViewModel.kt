package com.shanan.lufthansa.ui.map


import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shanan.lufthansa.data.locations.LocationsRepository
import com.shanan.lufthansa.model.Airport

/**
 * ViewModel for the [MapActivity] screen.
 * The ViewModel works with the [LocationsRepository] to get airports' locations.
 */
class MapViewModel(private val repository: LocationsRepository) : ViewModel() {

    val loadingVisibility = MutableLiveData<Int>()
    val emptyListVisibility = MutableLiveData<Int>()

    val searchResults: MutableLiveData<List<Airport>> = repository.searchResults

    init {
        loadingVisibility.value = View.VISIBLE
        emptyListVisibility.value = View.GONE

        searchResults.observeForever {
            loadingVisibility.value = View.GONE
            if (it.isEmpty()) {
                emptyListVisibility.value = View.VISIBLE
            } else {
                emptyListVisibility.value = View.GONE
            }
        }
    }

    fun search(codes: List<String>) {
        repository.getAirportsListByCodes(codes)
    }
}