package com.shanan.lufthansa.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.shanan.lufthansa.R
import com.shanan.lufthansa.databinding.ActivityMapBinding
import com.shanan.lufthansa.injection.Injection
import com.shanan.lufthansa.utils.Constants
import kotlin.math.roundToInt


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: MapViewModel
    private lateinit var binding: ActivityMapBinding
    private var errorSnackBar: Snackbar? = null

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val airportsCodes = intent?.extras?.getStringArrayList(Constants.IntentPassing.AIRPORTS_CODES)

        viewModel = ViewModelProviders.of(this,
                Injection.provideViewModelFactory(this, MapViewModel::class.java))
                .get(MapViewModel::class.java)
        binding.viewModel = viewModel

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        val airportsCodes = intent?.extras?.getStringArrayList(Constants.IntentPassing.AIRPORTS_CODES)
        viewModel.search(airportsCodes?.toList()!!)
        viewModel.searchResults.observe(this, Observer {

            googleMap.setOnMapLoadedCallback {

                val coordinates: MutableList<LatLng> = ArrayList()

                it.asIterable().forEach {
                    val latLng = LatLng(it.position.coordinate.latitude!!,
                            it.position.coordinate.longitude!!)
                    coordinates.add(latLng)
                    googleMap.addMarker(MarkerOptions()
                            .position(latLng)
                            .draggable(false)
                            .title(it.cityCode)
                            .snippet((it.names.name.value).plus(", ").plus(it.countryCode)))
                }

                val schedulePolyline = googleMap.addPolyline(PolylineOptions()
                        .clickable(true)
                        .addAll(coordinates))

                schedulePolyline.startCap = RoundCap()
                schedulePolyline.width = 8f
                schedulePolyline.color = Color.BLUE
                schedulePolyline.jointType = JointType.ROUND
                schedulePolyline.endCap = RoundCap()

                // Position the map's camera to a center point with the appropriate zoom level
                val width = resources.displayMetrics.widthPixels
                val padding = (width * 0.15).roundToInt()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(buildBounds(coordinates), padding))
            }
        })
    }

    private fun buildBounds(coordinates: List<LatLng>): LatLngBounds {

        val builder = LatLngBounds.Builder()
        for (coordinate in coordinates) {
            builder.include(coordinate)
        }
        return builder.build()
    }

    private fun showSnackBar(message: String) {
        errorSnackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        errorSnackBar?.show()
    }
}
