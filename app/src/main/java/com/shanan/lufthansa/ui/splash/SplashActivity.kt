package com.shanan.lufthansa.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.shanan.lufthansa.R
import com.shanan.lufthansa.databinding.ActivitySplashBinding
import com.shanan.lufthansa.injection.Injection
import com.shanan.lufthansa.model.AuthResponse
import com.shanan.lufthansa.utils.Constants
import com.shanan.lufthansa.utils.extension.Utils

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var binding: ActivitySplashBinding
    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))
                .get(SplashViewModel::class.java)

        var isAirportsCached = Utils.getBooleanFromPrefs(Constants.Prefs.IS_AIRPORTS_CACHED, this)

        viewModel.authResult.data.observe(this, Observer<AuthResponse> {
            viewModel.getAirports(it.access_token, isAirportsCached)
        })

        viewModel.authResult.networkErrors.observe(this, Observer<String> {
            Log.d("Activity", "error : ${it}")
            errorSnackbar = Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
            errorSnackbar?.setAction(R.string.retry, { viewModel.auth() })
            errorSnackbar?.show()
        })

        viewModel.isAirportsCached.observe(this, Observer {

            Utils.saveBooleanToPrefs(Constants.Prefs.IS_AIRPORTS_CACHED, it, this)
            if (it) {

            }
        })

        binding.viewModel = viewModel
    }
}
