package com.example.budgetmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        val appLogoImageViewSplashScreen = findViewById<ImageView>(R.id.appLogoImageViewSplashScreen)
        appLogoImageViewSplashScreen.alpha = 0f
        appLogoImageViewSplashScreen.animate().setDuration(1000).alpha(1f).withEndAction {
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()

        }


    }
}