package com.shanan.lufthansa

import android.app.Application
import com.shanan.lufthansa.injection.Injection

class App : Application(){
    val injector = Injection()
}
