package com.example.budgetmanager

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_settings_activity)

        // Variables
        val switchNightMode: SwitchCompat = findViewById(R.id.switchNightMode)
        val linearLayoutForNightMode: LinearLayout = findViewById(R.id.linearLayoutForNightMode)

        // Don’t forget that the system theme only supports the Android Pie (9.0) version,
        // so you need to remove the system theme option from UI for previous Android versions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) // Android Pie or Above
        {
            linearLayoutForNightMode.visibility = View.VISIBLE
        } else if (Build.VERSION.SDK_INT <=  27) // Android Oreo or below
        {
            linearLayoutForNightMode.visibility = View.GONE
        }


        // Night Mode Activation Code
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())


        var currentThemeMode = AppCompatDelegate.getDefaultNightMode()
        switchNightMode.isChecked = currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES

        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                restartCurrentActivity()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                restartCurrentActivity()
            }
        } // switchNightMode setOnCheckedChangeLister Code
    } // onCreate Code

    private fun restartCurrentActivity() {
        startActivity(Intent(this, this::class.java))
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    } // restartCurrentActivity Code
}