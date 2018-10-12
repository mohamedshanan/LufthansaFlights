package com.shanan.lufthansa.ui.flights

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shanan.lufthansa.R
import com.shanan.lufthansa.injection.Injection
import com.shanan.lufthansa.model.FlightRequest
import com.shanan.lufthansa.model.FlightsResponse
import com.shanan.lufthansa.utils.Constants.IntentPassing.FLIGHT_PARAMETERS
import kotlinx.android.synthetic.main.activity_flights.*

class FlightsActivity : AppCompatActivity() {

    private lateinit var viewModel: FlightsViewModel
    private val adapter = FlightsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flights)

        val request = intent.getSerializableExtra(FLIGHT_PARAMETERS) as FlightRequest

        // get the view model
        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this, FlightsViewModel::class.java))
                .get(FlightsViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)
        setupScrollListener()

        initAdapter()
//        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        viewModel.searchRepo(request)

    }

    private fun initAdapter() {
        list.adapter = adapter
        viewModel.flightsResult.observe(this, Observer<FlightsResponse> {
            showEmptyList(it?.ScheduleResource?.schedule?.size == 0)
            adapter.submitList(it?.ScheduleResource?.schedule)
        })
        viewModel.error.observe(this, Observer<String> {
            Toast.makeText(this, "\uD83D\uDE28 Wooops ${it}", Toast.LENGTH_LONG).show()
        })
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            emptyList.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
    }

    private fun setupScrollListener() {
        val layoutManager = list.layoutManager as LinearLayoutManager
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }
}
