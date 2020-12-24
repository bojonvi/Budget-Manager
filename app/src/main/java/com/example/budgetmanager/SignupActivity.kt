package com.example.budgetmanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        statusBarColor()

    }

    private fun statusBarColor() {
        // Set Status Bar Color first, in this case it will be dark_desaturated_blue ALWAYS
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_desaturated_blue)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // If the device is running Marshmallow
            window.statusBarColor = resources.getColor(R.color.dark_desaturated_blue, this.theme)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Else if device is running  Lollipop
            ContextCompat.getColor(this, R.color.dark_desaturated_blue)
        }
    }
}