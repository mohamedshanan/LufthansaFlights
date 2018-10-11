package com.shanan.lufthansa.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.shanan.lufthansa.R
import com.shanan.lufthansa.databinding.ActivitySplashBinding
import com.shanan.lufthansa.injection.Injection
import com.shanan.lufthansa.model.AuthResponse

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))
                .get(SplashViewModel::class.java)

        viewModel.authResult.data.observe(this, Observer<AuthResponse> {

        })

        viewModel.authResult.networkErrors.observe(this, Observer<String> {
            Log.d("Activity", "error : ${it}")
        })

        binding.viewModel = viewModel
    }
}
