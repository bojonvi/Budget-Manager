package com.example.budgetmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.budgetmanager.database.DatabaseHelper
import java.lang.Exception

class CreateBudgetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_budget_activity)

        // Variables
        val databaseHelper = DatabaseHelper(this)
        val availableMoney = intent.getStringExtra("availableMoney")
        val createBudgetAvailableBalance: TextView =
            findViewById(R.id.createBudgetPreviewDashboardAvailableBalance)
        val createBudgetTitleField: EditText =
            findViewById(R.id.createBudgetTitleField)
        val createBudgetDescriptionField: EditText =
            findViewById(R.id.createBudgetDescriptionField)
        val createBudgetMoneyField: EditText =
            findViewById(R.id.createBudgetMoneyField)


        // UI Modification
        createBudgetAvailableBalance.text = availableMoney

        // Done Function
        findViewById<ImageView>(R.id.doneImageButton).setOnClickListener{
            // Variables
            val createBudgetTitleFieldString: String =
                createBudgetTitleField.text.toString()
            val createBudgetDescriptionFieldString: String =
                createBudgetDescriptionField.text.toString()
            val createBudgetMoneyFieldFloat: Float

            // Input Checker
            try{
                createBudgetMoneyFieldFloat = createBudgetMoneyField.text.toString().toFloat()
//                Toast.makeText(this, createBudgetTitleFieldString, Toast.LENGTH_SHORT).show()
                if (createBudgetTitleFieldString.isEmpty()){
                    createBudgetTitleField.error = "Title must not be empty!"
                } else if (createBudgetMoneyFieldFloat > availableMoney!!.toFloat()){
                    createBudgetMoneyField.error = "Insufficient Allowance"
                } else {
                    databaseHelper.createBudget(createBudgetTitleFieldString, createBudgetMoneyFieldFloat.toString(), createBudgetDescriptionFieldString)
                    finish()
                }
            } catch (e: Exception){
                if (createBudgetMoneyField.text.toString().isEmpty()){
                    createBudgetMoneyField.error = "No Money Inputted"
                } else {
                    createBudgetMoneyField.error = "Incorrect Money Input"
                }
            }
        }

        // Back Function
        findViewById<ImageView>(R.id.backImageButton).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

    }
}