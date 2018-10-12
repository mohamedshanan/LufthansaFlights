package com.shanan.lufthansa.ui.splash

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.shanan.lufthansa.R
import com.shanan.lufthansa.databinding.ActivitySplashBinding
import com.shanan.lufthansa.injection.Injection
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.model.AuthResponse
import com.shanan.lufthansa.utils.Constants
import com.shanan.lufthansa.utils.extension.Utils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var binding: ActivitySplashBinding
    private var errorSnackbar: Snackbar? = null
    private var fromAirport: Airport? = null
    private var toAirport: Airport? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))
                .get(SplashViewModel::class.java)

        binding.viewModel = viewModel

        addObservers()
        addAirportsObservers()
    }

    private fun addObservers() {
        var isAirportsCached = Utils.getBooleanFromPrefs(Constants.Prefs.IS_AIRPORTS_CACHED, this)

        viewModel.authResult.data.observe(this, Observer<AuthResponse> {
            viewModel.getAirports(it.access_token, isAirportsCached)
        })

        viewModel.authResult.networkErrors.observe(this, Observer<String> {
            Log.d("_SplashActivity_", "error : ${it}")
            showSnackBar(it)
        })
    }

    private fun addAirportsObservers() {

        viewModel.isAirportsCached.observe(this, Observer {

            Utils.saveBooleanToPrefs(Constants.Prefs.IS_AIRPORTS_CACHED, it, this)
            if (it) {

                val watcher = object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun afterTextChanged(editable: Editable) {
                        if (editable.length > 1) {
                            viewModel.search(editable)
                        }
                    }
                }

                binding.from.addTextChangedListener(watcher)
                binding.to.addTextChangedListener(watcher)

                binding.searchButton.setOnClickListener {
                    if (fromAirport != null && toAirport != null) {
                        if (!fromAirport?.airportCode.equals(toAirport?.airportCode)) {
                            viewModel.getFlights(fromAirport?.airportCode, toAirport?.airportCode)
                        } else {
                            showSnackBar(getString(R.string.same_source_destination))
                        }
                    } else {
                        showSnackBar(getString(R.string.same_source_destination))
                    }
                }

                viewModel.searchResults.observe(this, Observer {

                    var nameMap: List<String> = it.map { it.names.name.value }
                    val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, nameMap)

                    if (from.isFocused) {
                        from.setAdapter(adapter)
                    } else if (to.isFocused) {
                        to.setAdapter(adapter)
                    }

                    binding.from.onItemClickListener = object : AdapterView.OnItemClickListener {
                        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            fromAirport = it[position]
                        }
                    }

                    binding.to.onItemClickListener = object : AdapterView.OnItemClickListener {
                        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            toAirport = it[position]
                        }
                    }
                })
            }
        })
    }

    fun showSnackBar(message: String) {
        errorSnackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.setAction(R.string.retry, { viewModel.auth() })
        errorSnackbar?.show()
    }
}
