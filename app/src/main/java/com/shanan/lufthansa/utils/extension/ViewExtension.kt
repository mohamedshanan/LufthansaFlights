package com.shanan.lufthansa.utils.extension

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun View.getParentActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun Context.toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()