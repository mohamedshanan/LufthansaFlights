package com.shanan.lufthansa.ui.landing

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.shanan.lufthansa.App
import com.shanan.lufthansa.R
import com.shanan.lufthansa.databinding.ActivitySplashBinding
import com.shanan.lufthansa.injection.Injection
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.model.AuthResponse
import com.shanan.lufthansa.ui.schedules.SchedulesActivity
import com.shanan.lufthansa.utils.Constants
import com.shanan.lufthansa.utils.Constants.IntentPassing.FLIGHT_PARAMETERS
import com.shanan.lufthansa.utils.Utils
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.layout_error.*
import java.io.Serializable

class LandingActivity : AppCompatActivity() {

    private lateinit var viewModel: LandingViewModel
    private lateinit var binding: ActivitySplashBinding
    private var errorSnackBar: Snackbar? = null
    private var fromAirport: Airport? = null
    private var toAirport: Airport? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        val injector = (application as App).injector
        viewModel = ViewModelProviders.of(this, injector.provideViewModelFactory(this, LandingViewModel::class.java))
                .get(LandingViewModel::class.java)
        binding.viewModel = viewModel

        val adapter = AutoCompleteAdapter(this, R.layout.item_airport, R.layout.item_airport, ArrayList<Airport>())
        from.setAdapter(adapter)
        to.setAdapter(adapter)

        addObservers()
        addAirportsObservers()
    }

    private fun addObservers() {
        val isAirportsCached = Utils.getBooleanFromPrefs(Constants.Prefs.IS_AIRPORTS_CACHED, this)

        viewModel.authResult.data.observe(this, Observer<AuthResponse> {
            viewModel.getAirports(it.access_token, isAirportsCached)
        })

        viewModel.authResult.error.observe(this, Observer<String> {
            showSnackBar(it)
            showError(it, getString(R.string.retry), View.OnClickListener {
                layout_error_view.visibility = View.GONE
                viewModel.authenticate()
            })
        })
    }

    private fun addAirportsObservers() {

        viewModel.isAirportsCached.observe(this, Observer {

            Utils.saveBooleanToPrefs(Constants.Prefs.IS_AIRPORTS_CACHED, it, this)

            if (it) {

                viewModel.setDate()

                binding.date.setOnClickListener {
                    Utils.showFutureDateDialog(this, viewModel)
                }

                val watcher = object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                    }

                    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                        if (charSequence.length > 2) {
                            viewModel.search(charSequence)
                        }
                    }

                    override fun afterTextChanged(editable: Editable) {}
                }

                binding.from.addTextChangedListener(watcher)
                binding.to.addTextChangedListener(watcher)
                binding.from.threshold = 3
                binding.from.threshold = 3
                binding.searchButton.setOnClickListener {
                    viewModel.getFlights(fromAirport?.airportCode, toAirport?.airportCode)
                }

                viewModel.searchResults.observe(this, Observer {

                    if (from.isFocused) {
                        (from.adapter as AutoCompleteAdapter).setItems(it)
                    } else if (to.isFocused) {
                        (from.adapter as AutoCompleteAdapter).setItems(it)
                    }

                    binding.from.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                        fromAirport = it[position]
                        from.setText(it[position].names.name.value)
                    }
                    binding.to.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                        toAirport = it[position]
                        to.setText(it[position].names.name.value)
                    }
                })
            }
        })

        viewModel.flightRequest.observe(this, Observer {
            val intent = Intent(this, SchedulesActivity::class.java)
            intent.putExtra(FLIGHT_PARAMETERS, it as Serializable)
            startActivity(intent)
        })
    }

    private fun showError(textString: String, @Nullable actionString: String?,
                          actionListener: View.OnClickListener? = null) {
        viewModel.loadingVisibility.value = View.GONE
        viewModel.searchVisibility.value = View.GONE
        layout_error_view.visibility = View.VISIBLE

        if (actionString != null) {
            layout_error_action.setOnClickListener(actionListener)
            layout_error_action.visibility = View.VISIBLE
        }

        layout_error_action.text = actionString
        layout_error_text.text = textString
    }


    private fun showSnackBar(message: String) {
        errorSnackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        errorSnackBar?.show()
    }

}
