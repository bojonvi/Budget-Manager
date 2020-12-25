package com.example.budgetmanager

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {


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

    private fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false }
        } else { // Else code is for devices running Lollipop and Marshmallow where IT IS NOT yet depreciated
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    } } } }
        return result }

    private fun validateEmail(registerEmailField: String): Boolean {
        return !TextUtils.isEmpty(registerEmailField) && Patterns.EMAIL_ADDRESS.matcher(
            registerEmailField
        )
            .matches()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        statusBarColor()

        // Variables
        val registerEmailField: TextInputEditText = findViewById(R.id.register_emailField)
        val registerUsernameField: TextInputEditText = findViewById(R.id.register_usernameField)
        val registerPasswordField: TextInputEditText = findViewById(R.id.register_passwordField)
        val registerConfirmPasswordField: TextInputEditText =
            findViewById(R.id.register_confirmPasswordField)
        val genderSelectRadioGroup: RadioGroup = findViewById(R.id.genderSelectRadioGroup)

        // Get and Convert variables to String
        val registerEmailFieldString = registerEmailField.text.toString()
        val registerUsernameFieldString = registerUsernameField.text.toString()
        val registerPasswordFieldString = registerPasswordField.text.toString()
        val registerConfirmPasswordFieldString = registerConfirmPasswordField.text.toString()

        genderSelectRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.male_selectGender) {
                Toast.makeText(this, "Gender Selected: Male", Toast.LENGTH_SHORT).show()
            } else if (checkedId == R.id.female_selectGender) {
                Toast.makeText(this, "Gender Selected: Female", Toast.LENGTH_SHORT).show()
            }
        } // genderSelectRadioGroup.setOnCheckedChangeListener {}



        findViewById<Button>(R.id.signUp_signUpButton).setOnClickListener {
            if (isInternetAvailable(this)) {

            }
            else {
                val internetValidationDialogInterface = AlertDialog.Builder(this)
                // set message of alert dialog
                internetValidationDialogInterface.setMessage(
                    "Make sure that WI-FI or Mobile Data is turned on, then try again.\n" +
                            "You cannot Sign Up Account without an Internet Connection.")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Retry") { _: DialogInterface, _: Int ->
                        recreate() }
                    // negative button text and action
                    .setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                        recreate() }
                // create dialog box
                val internetValidationAlert = internetValidationDialogInterface.create()
                // set title for alert dialog box
                internetValidationAlert.setTitle("No Internet Connection")
                internetValidationAlert.setIcon(R.mipmap.ic_launcher)
                // show alert dialog
                internetValidationAlert.show()
            }
        }

    }

    // override fun onCreate() {}

}