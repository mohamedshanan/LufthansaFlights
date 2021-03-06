package com.shanan.lufthansa.ui.map

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.shanan.lufthansa.App
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
        supportActionBar?.title = "${airportsCodes?.get(0)} - ${airportsCodes?.get(airportsCodes.size - 1)}"

        val injector = (application as App).injector
        viewModel = ViewModelProviders.of(this,
                injector.provideViewModelFactory(this, MapViewModel::class.java))
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

                it.asIterable().forEach { airport ->
                    val latLng = LatLng(airport.position.coordinate.latitude!!,
                            airport.position.coordinate.longitude!!)
                    coordinates.add(latLng)
                    googleMap.addMarker(MarkerOptions()
                            .position(latLng)
                            .draggable(false)
                            .title(airport.airportCode)
                            .snippet((airport.names.name.value).plus(", ").plus(airport.countryCode))
                            .icon(bitmapDescriptorFromVector(this,
                                    (airport.cityCode))))
                }

                val schedulePolyline = googleMap.addPolyline(PolylineOptions()
                        .clickable(true)
                        .addAll(coordinates))

                schedulePolyline.startCap = RoundCap()
                schedulePolyline.width = 5f
                schedulePolyline.color = ContextCompat.getColor(this, R.color.colorPrimary)
                schedulePolyline.jointType = JointType.DEFAULT
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

    private fun bitmapDescriptorFromVector(context: Context, txt: String): BitmapDescriptor {
        val background = ContextCompat.getDrawable(context, R.drawable.ic_marker_bg)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)

        val bitmap = Bitmap.createBitmap(background.intrinsicWidth, background.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        background.draw(canvas)

        val textPaint = Paint()
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.textSize = 30f
        textPaint.color = Color.BLACK
        textPaint.measureText(txt)

        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

        canvas.drawText(txt, 15f, yPos, textPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}
