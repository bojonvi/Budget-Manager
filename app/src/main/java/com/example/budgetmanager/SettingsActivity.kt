package com.example.budgetmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_settings_activity)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Variables
        val switchNightMode: SwitchCompat = findViewById(R.id.switchNightMode)
        val profilePictureImage: ImageView = findViewById(R.id.profilePictureImage)
        val linearLayoutForNightMode: LinearLayout = findViewById(R.id.linearLayoutForNightMode)
        val userUIDFieldView: TextView = findViewById(R.id.userUIDFieldView)
        val userNameFieldView: TextView = findViewById(R.id.userNameFieldView)
        val emailAddressFieldView: TextView = findViewById(R.id.emailAddressFieldView)
        val copyTextToClipboardImageButton: ImageView =
            findViewById(R.id.copyTextToClipboardImageButton)


        // Don’t forget that the system theme only supports the Android Pie (9.0) version,
        // so you need to remove the system theme option from UI for previous Android versions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) // Android Pie or Above
        {
            linearLayoutForNightMode.visibility = View.VISIBLE
        } else if (Build.VERSION.SDK_INT <= 27) // Android Oreo or below
        {
            linearLayoutForNightMode.visibility = View.GONE
        }


        // Night Mode Activation Code
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())


        val currentThemeMode = AppCompatDelegate.getDefaultNightMode()
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

        Glide.with(this).load(currentUser?.photoUrl).apply(RequestOptions.circleCropTransform())
            .into(profilePictureImage)
        userUIDFieldView.text = currentUser?.uid
        userNameFieldView.text = currentUser?.displayName
        emailAddressFieldView.text = currentUser?.email

        copyTextToClipboardImageButton.setOnClickListener {
            copyTextToClipboard()
        }

    } // onCreate Code

    private fun copyTextToClipboard() {
        try {
            val userUIDFieldView: TextView = findViewById(R.id.userUIDFieldView)
            val textToCopy = userUIDFieldView.text
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to copy text. Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restartCurrentActivity() {
        startActivity(Intent(this, this::class.java))
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    } // restartCurrentActivity Code
}