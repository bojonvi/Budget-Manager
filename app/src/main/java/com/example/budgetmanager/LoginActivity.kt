package com.example.budgetmanager

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private var backToast: Toast? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    var callbackManager = CallbackManager.Factory.create()


    companion object {
        private const val RC_SIGN_IN = 120
    }

    // Press back again to EXIT APPLICATION
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast!!.cancel()
            finishAffinity()
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
            updateUI(user)
        } else {
            Log.e("USER STATUS", "No user currently logged in.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        statusBarColor()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Variables
        val loginEmailField: TextInputEditText = findViewById(R.id.login_emailField)
        val loginPasswordField: TextInputEditText = findViewById(R.id.login_passwordField)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1027910760397-rdkmsf9bui9bcort1p1s28lbj045stah.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


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
                    try {
                        auth.signInWithEmailAndPassword(
                            loginEmailFieldString,
                            loginPasswordFieldString
                        )
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("SIGN IN STATUS", "signInWithEmail:success")
                                    val user = auth.currentUser
                                    updateUI(user)
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(
                                        "SIGN IN STATUS",
                                        "signInWithEmail:failure",
                                        task.exception
                                    )
                                    Toast.makeText(
                                        baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    updateUI(null)
                                }

                            }
                    } catch (e: Exception) {
                        var errorMessage = e.message
                        Toast.makeText(
                            this,
                            "There was a problem signing in.\n $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
        findViewById<MaterialButton>(R.id.login_signUpButton).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.login_forgotPasswordButton).setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        findViewById<ImageView>(R.id.login_facebook_ImageButton).setOnClickListener {
            printHashKey(this)
            facebookLogin()
        }

        findViewById<ImageView>(R.id.login_google_ImageButton).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("GOOGLE FB AUTH", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("GOOGLE FB AUTH", "Google sign in failed", e)
                    // ...
                }
            } else {
                Log.w("LoginActivity", exception.toString())
            }
        }
    } // onActivityResult() {}

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }

                // ...
            }
    }

    private fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    firebaseAuthWithFacebook(result)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }
            })
    }

    private fun firebaseAuthWithFacebook(result: LoginResult?) {
        var credential = FacebookAuthProvider.getCredential(result?.accessToken?.token!!)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { facebookTask ->
                if (facebookTask.isSuccessful) {
                    Toast.makeText(this, "Facebook Authentication Successful", Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                } else {
                    Toast.makeText(this, "Failed to Authentication Facebook", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                println("printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("FACEBOOK AUTH", "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e("FACEBOOK AUTH", "printHashKey()", e)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val loginEmailField: TextInputEditText = findViewById(R.id.login_emailField)
        val loginEmailFieldString: String = loginEmailField.text.toString()

        if (user != null) {
            if (user.isEmailVerified) {
                val intent = Intent(Intent(this, DashboardActivity::class.java))
                intent.putExtra("emailAddress", loginEmailFieldString)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Before signing in to your account, please verify your email provided first.",
                    Toast.LENGTH_LONG
                ).show()
                Firebase.auth.signOut()
            }

        }
    }


}
