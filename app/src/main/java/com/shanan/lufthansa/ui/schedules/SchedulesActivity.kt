package com.shanan.lufthansa.ui.schedules

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shanan.lufthansa.App
import com.shanan.lufthansa.R
import com.shanan.lufthansa.databinding.ActivityFlightsBinding
import com.shanan.lufthansa.injection.Injection
import com.shanan.lufthansa.model.FlightsResponse
import com.shanan.lufthansa.model.ScheduleRequest
import com.shanan.lufthansa.utils.Constants.IntentPassing.FLIGHT_PARAMETERS
import kotlinx.android.synthetic.main.activity_flights.*

class SchedulesActivity : AppCompatActivity() {

    private lateinit var viewModel: SchedulesViewModel
    private lateinit var binding: ActivityFlightsBinding
    private var errorSnackBar: Snackbar? = null
    private val adapter = SchedulesAdapter()

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_flights)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val injector = (application as App).injector

        viewModel = ViewModelProviders.of(this,
                injector.provideViewModelFactory(this, SchedulesViewModel::class.java))
                .get(SchedulesViewModel::class.java)
        binding.viewModel = viewModel

        initRecyclerView()
        initAdapter()

        val request = intent.getSerializableExtra(FLIGHT_PARAMETERS) as ScheduleRequest
        supportActionBar?.title = "${request.origin} - ${request.destination}"
        viewModel.searchRepo(request)

    }

    private fun initRecyclerView() {
        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)
        setupScrollListener()
    }

    private fun initAdapter() {
        list.adapter = adapter
        viewModel.flightsResult.observe(this, Observer<FlightsResponse> {
            showEmptyList(it?.ScheduleResource?.schedule?.size == 0)
            adapter.submitList(it?.ScheduleResource?.schedule)
        })
        viewModel.error.observe(this, Observer<String> {
            if ((list.adapter as SchedulesAdapter).itemCount == 0) {
                showEmptyList(true)
            } else {
                showSnackBar(it)
            }
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
        val layoutManager = LinearLayoutManager(this)
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

    private fun showSnackBar(message: String) {
        errorSnackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        errorSnackBar?.show()
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }
}
