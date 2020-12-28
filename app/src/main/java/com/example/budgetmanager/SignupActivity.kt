package com.example.budgetmanager

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreAuth: FirebaseFirestore

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
                else -> false
            }
        } else { // Else code is for devices running Lollipop and Marshmallow where IT IS NOT yet depreciated
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }

    private fun isValidEmail(registerEmailFieldString: String): Boolean {
        return !TextUtils.isEmpty(registerEmailFieldString) && Patterns.EMAIL_ADDRESS.matcher(
            registerEmailFieldString
        )
            .matches()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        statusBarColor()
        auth = Firebase.auth
        firestoreAuth = FirebaseFirestore.getInstance()

        // Variables
        val registerEmailField: EditText = findViewById(R.id.register_emailField)
        val registerUsernameField: EditText = findViewById(R.id.register_usernameField)
        val registerPasswordField: EditText = findViewById(R.id.register_passwordField)
        val registerConfirmPasswordField: EditText =
            findViewById(R.id.register_confirmPasswordField)
        val genderSelectRadioGroup: RadioGroup = findViewById(R.id.genderSelectRadioGroup)
        val signUpButton: Button = findViewById(R.id.signUp_signUpButton)



        genderSelectRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.male_selectGender) {
                Toast.makeText(this, "Gender Selected: Male", Toast.LENGTH_SHORT).show()
            } else if (checkedId == R.id.female_selectGender) {
                Toast.makeText(this, "Gender Selected: Female", Toast.LENGTH_SHORT).show()
            }
        } // genderSelectRadioGroup.setOnCheckedChangeListener {}


        signUpButton.setOnClickListener {

            // Get and Convert variables to String
            val registerEmailFieldString: String = registerEmailField.text.toString()
            val registerUsernameFieldString: String = registerUsernameField.text.toString()
            val registerPasswordFieldString: String = registerPasswordField.text.toString()
            val registerConfirmPasswordFieldString: String =
                registerConfirmPasswordField.text.toString()

            if (isInternetAvailable(this)) {
                if (registerPasswordFieldString != registerConfirmPasswordFieldString) {
                    Toast.makeText(
                        this,
                        "Your Password and Confirm Password do not match",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (registerEmailFieldString.trim()
                        .isEmpty() || registerPasswordFieldString.trim()
                        .isEmpty() || registerConfirmPasswordFieldString.trim()
                        .isEmpty() || registerUsernameFieldString.trim()
                        .isEmpty()
                ) {
                    Toast.makeText(this, "Please input all fields", Toast.LENGTH_SHORT).show()
                } else if (registerEmailFieldString.trim()
                        .isNotEmpty() || registerPasswordFieldString.trim()
                        .isNotEmpty() || registerConfirmPasswordFieldString.trim()
                        .isNotEmpty() || registerUsernameFieldString.trim().isNotEmpty()
                ) {
                    signUpUser(
                        registerEmailFieldString,
                        registerPasswordFieldString
                    )
                } else if (!isValidEmail(registerEmailFieldString)) {
                    registerEmailField.error = "Please input a valid Email Address"
                    return@setOnClickListener
                } else {
                    Toast.makeText(this, "Please input all required fields", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val internetValidationDialogInterface = AlertDialog.Builder(this)
                // set message of alert dialog
                internetValidationDialogInterface.setMessage(
                    "Make sure that WI-FI or Mobile Data is turned on, then try again.\n" +
                            "You cannot Sign Up Account without an Internet Connection."
                )
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Retry") { _: DialogInterface, _: Int ->
                        recreate() }
                    // negative button text and action
                    .setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                        recreate()
                    }
                // create dialog box
                val internetValidationAlert = internetValidationDialogInterface.create()
                // set title for alert dialog box
                internetValidationAlert.setTitle("No Internet Connection")
                internetValidationAlert.setIcon(R.mipmap.ic_launcher)
                // show alert dialog
                internetValidationAlert.show()
            }
        } // findViewById<Button>(R.id.signUp_signUpButton).setOnClickListener {}

        findViewById<MaterialButton>(R.id.signUp_logInInsteadButton).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    } // override fun onCreate() {}

    private fun signUpUser(registerEmailFieldString: String, registerPasswordFieldString: String) {
        // Find ID in the xml file
        val registerEmailField: EditText = findViewById(R.id.register_emailField)
        val registerPasswordField: EditText = findViewById(R.id.register_passwordField)

        auth.createUserWithEmailAndPassword(
            registerEmailFieldString.trim(),
            registerPasswordFieldString.trim()
        )
            .addOnCompleteListener(this) { signUpTask ->
                if (signUpTask.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener(this) { theTask ->
                            if (theTask.isSuccessful) {
                                Firebase.auth.signOut()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "There was an error creating the account: " + theTask.exception,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else if (!signUpTask.isSuccessful) {
                    try {
                        throw signUpTask.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            this,
                            "The account [ $registerEmailFieldString ] has been already registered in the System.",
                            Toast.LENGTH_SHORT
                        ).show()
                        registerEmailField.requestFocus()
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        Toast.makeText(
                            this,
                            "Weak Password. Input at-least 6 characters.",
                            Toast.LENGTH_SHORT
                        ).show()
                        registerPasswordField.requestFocus()
                    } catch (e: Exception) {
                        Log.e(this.toString(), e.message.toString())
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Account is unable to register. Please try again. \n" + signUpTask.exception,
                        Toast.LENGTH_LONG
                    ).show()
                }
                finish()
            }
    }
}
