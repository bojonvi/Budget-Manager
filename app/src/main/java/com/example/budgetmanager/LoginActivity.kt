package com.example.budgetmanager

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private var backToast: Toast? = null
    private lateinit var auth: FirebaseAuth



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

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            Log.e("USER STATUS:", "User Null or Not Logged In")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        statusBarColor()
        auth = Firebase.auth

        // Variables
        val loginEmailField: TextInputEditText = findViewById(R.id.login_emailField)
        val loginPasswordField: TextInputEditText = findViewById(R.id.login_passwordField)

        findViewById<MaterialButton>(R.id.loginButton).setOnClickListener {
            val loginEmailFieldString: String = loginEmailField.text.toString()
            val loginPasswordFieldString: String = loginPasswordField.text.toString()

            if (isInternetAvailable(this)) {
                if (loginEmailFieldString.trim().isEmpty()) {
                    loginEmailField.error = "Input your Email Address"
                    loginEmailField.requestFocus()
                    return@setOnClickListener
                } else if (loginPasswordFieldString.trim().isEmpty()) {
                    loginPasswordField.error = "Input your Password"
                    loginPasswordField.requestFocus()
                    return@setOnClickListener
                } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmailFieldString).matches()) {
                    loginEmailField.error = "Input a valid Email Address"
                    loginEmailField.requestFocus()
                    return@setOnClickListener
                } else {

                    auth.signInWithEmailAndPassword(loginEmailFieldString, loginPasswordFieldString)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success")
                                val user = auth.currentUser
                                updateUI(user)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                                updateUI(null)
                            }

                        }
                }

            } else {
                val internetValidationDialogInterface = AlertDialog.Builder(this)
                // set message of alert dialog
                internetValidationDialogInterface.setMessage(
                    "Make sure that WI-FI or Mobile Data is turned on, then try again.\n" +
                    "You cannot Sign Up Account without an Internet Connection.")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Retry") { _: DialogInterface, _: Int -> recreate() }
                    // negative button text and action
                    .setNegativeButton("Cancel") { _: DialogInterface, _: Int -> recreate() }
                // create dialog box
                val internetValidationAlert = internetValidationDialogInterface.create()
                // set title for alert dialog box
                internetValidationAlert.setTitle("No Internet Connection")
                internetValidationAlert.setIcon(R.mipmap.ic_launcher)
                // show alert dialog
                internetValidationAlert.show()
            } // Else
        } // findViewById<MaterialButton>(R.id.loginButton).setOnClickListener{}
        findViewById<MaterialButton>(R.id.login_signUpButton).setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.login_forgotPasswordButton).setOnClickListener{
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }

    private fun updateUI(user: FirebaseUser?, ) {
        val loginEmailField: TextInputEditText = findViewById(R.id.login_emailField)
        val loginEmailFieldString: String = loginEmailField.text.toString()
        if (user != null) {
            if (user.isEmailVerified) {
                val intent = Intent(Intent(this, DashboardActivity::class.java))
                intent.putExtra("emailAddress", loginEmailFieldString)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Email is not verified. Please verify email first.", Toast.LENGTH_LONG).show()
            }

        }
    }


}




