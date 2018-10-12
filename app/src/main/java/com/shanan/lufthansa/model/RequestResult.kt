package com.shanan.lufthansa.model

import androidx.lifecycle.MutableLiveData


class RequestResult<T>(
        var data: MutableLiveData<T>,
        var error: MutableLiveData<String>
)