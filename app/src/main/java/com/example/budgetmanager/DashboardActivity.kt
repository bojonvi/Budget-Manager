package com.example.budgetmanager

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)

        val settingsButtonTapped: Button = findViewById(R.id.dashboard_settingsButton)
        settingsButtonTapped.setOnClickListener {
            goToSettingsActivity()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.item_preference) {
            goToSettingsActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }


    fun showAddMoneyAlert(view: View) { // avoid warning error that is never used, it is actually used.
        val dashboardUserMoneyBalanceText: TextView = findViewById(R.id.dashboard_userMoneyBalance)

        val inflater = layoutInflater
        val inflaterView = inflater.inflate(R.layout.addmoney_dialog, null)

        val inputMoneyFieldText = inflaterView.findViewById(R.id.inputMoneyField) as EditText

        val addMoneyAlertDialog = AlertDialog.Builder(this)
        addMoneyAlertDialog.setTitle("Add Money Balance")
        addMoneyAlertDialog.setIcon(R.mipmap.ic_launcher)
        addMoneyAlertDialog.setView(inflaterView) // This set Custom XML in Alert Dialog
        addMoneyAlertDialog.setCancelable(false) // prevent cancel on outside touch of dialog

        addMoneyAlertDialog.setNegativeButton("Cancel") { dialog, which ->
            return@setNegativeButton
        }
        addMoneyAlertDialog.setPositiveButton("Add") { dialog, which ->
            try {
                val dashboardUserMoneyBalanceTextString =
                    dashboardUserMoneyBalanceText.text.toString().toDouble()
                val inputtedMoney = inputMoneyFieldText.text.toString().toDouble()
                val sumOfMoneyBalance =
                    (dashboardUserMoneyBalanceTextString + inputtedMoney).toString()
                dashboardUserMoneyBalanceText.text = formatDecimal(sumOfMoneyBalance)
                return@setPositiveButton
            } catch (e: Exception) {
                val numberFormatExceptionToast =
                    Toast.makeText(this@DashboardActivity, "No money inputted", Toast.LENGTH_SHORT)
                numberFormatExceptionToast.setGravity(Gravity.CENTER, 0, 0)
                numberFormatExceptionToast.show()
                return@setPositiveButton
            }
        }
        val addMoneyDialogBox = addMoneyAlertDialog.create()
        addMoneyDialogBox.show()

    }

    private fun formatDecimal(value: String?): String? {
        val df = DecimalFormat("#,###,##0.00")
        return df.format(java.lang.Double.valueOf(value!!))

    }
}