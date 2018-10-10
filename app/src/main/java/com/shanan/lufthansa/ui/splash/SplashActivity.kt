package com.shanan.lufthansa.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.shanan.lufthansa.R
import com.shanan.lufthansa.injection.Injection

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))
                .get(AuthViewModel::class.java)
        viewModel.auth()
    }
}
