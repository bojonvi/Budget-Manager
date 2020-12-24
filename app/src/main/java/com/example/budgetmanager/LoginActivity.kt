package com.example.budgetmanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private var backToast: Toast? = null

    // Press back again to EXIT APPLICATION
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast!!.cancel()
            finish()
            return
        } else {
            backToast =
                Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT)
            backToast!!.show()
        }
        backPressedTime = System.currentTimeMillis()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        statusBarColor()


        findViewById<MaterialButton>(R.id.login_signUpButton).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
        }

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