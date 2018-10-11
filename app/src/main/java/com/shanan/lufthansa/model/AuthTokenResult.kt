package com.shanan.lufthansa.model

import androidx.lifecycle.MutableLiveData


class AuthTokenResult(
        var data: MutableLiveData<AuthResponse>,
        var networkErrors: MutableLiveData<String>
)