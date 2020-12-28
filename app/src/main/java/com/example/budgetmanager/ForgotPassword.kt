package com.example.budgetmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val emailSentTextString =
    "We have sent an email message to your email address provided. " +
    "The password reset message will be delivered to your Inbox or Spam Folder shortly or within 24 hours."
const val emailSentTextStringError = "There was an error sending email to the server."

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)
        auth = Firebase.auth


        // Variables
        val forgotPasswordEmailField: TextInputEditText = findViewById(R.id.forgotPassword_EmailField)
        val forgotPasswordSendEmailButton: Button = findViewById(R.id.forgotPassword_sendEmailButton)
        val forgotPasswordBack: MaterialButton = findViewById(R.id.forgotPassword_Back)


        forgotPasswordSendEmailButton.setOnClickListener {
            // Show sent confirmation string
            if (forgotPasswordEmailField.text.toString().isEmpty()) {
                forgotPasswordEmailField.error = "Email Address is Empty. Please provide."
                forgotPasswordEmailField.requestFocus()
            } else {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                auth.sendPasswordResetEmail(forgotPasswordEmailField.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            auth.currentUser
                            findViewById<TextView>(R.id.emailConfirmText).text = emailSentTextString
                        } else {
                            findViewById<TextView>(R.id.emailConfirmText).text = emailSentTextStringError
                        }
                    }
            }
        }

        forgotPasswordBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}