package com.example.budgetmanager

import android.content.Intent
import android.graphics.Typeface
import android.nfc.FormatException
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.PopupMenu.OnMenuItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetmanager.database.DatabaseHelper
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.NumberFormatException
import java.text.DecimalFormat

class DashboardActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private var backToast: Toast? = null

    lateinit var accountTextView: TextView
    lateinit var databaseHelper: DatabaseHelper

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
        setContentView(R.layout.dashboard_activity)


        // Variables
        databaseHelper = DatabaseHelper(this)
        accountTextView = findViewById(R.id.dashboard_userMoneyBalance)

        // Data
        var stringBuffer = StringBuffer()
        val userData = databaseHelper.userAccount()
        val budgetData = databaseHelper.readData()
        while (userData.moveToNext()) {
            stringBuffer.append(userData.getString((2)))
        }
        accountTextView.text = stringBuffer.toString()


        val settingsButtonTapped: Button = findViewById(R.id.dashboard_settingsButton)
        settingsButtonTapped.setOnClickListener {
            goToSettingsActivity()
        }

        val dashboardHelpButtonTapped: Button = findViewById(R.id.dashboard_helpButton)
        dashboardHelpButtonTapped.setOnClickListener {
            val helpPopupMenu = PopupMenu(this@DashboardActivity, dashboardHelpButtonTapped)
            helpPopupMenu.menuInflater.inflate(R.menu.menu_popup, helpPopupMenu.menu)
            helpPopupMenu.setOnMenuItemClickListener(object : OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item!!.itemId) {
                        R.id.howToUseAppOption -> goToHowToUseApp()
                        R.id.privacyPolicyOption -> goToPrivacyPolicy()
                    }
                    return true
                }

                private fun goToPrivacyPolicy() {
                    val privacyPolicyIntent =
                        Intent(this@DashboardActivity, PrivacyPolicy::class.java)
                    startActivity(privacyPolicyIntent)
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
                }

                private fun goToHowToUseApp() {
                    val howToUseAppIntent =
                        Intent(this@DashboardActivity, SettingsActivity::class.java)
                    startActivity(howToUseAppIntent)
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
                }

            })
            helpPopupMenu.show()
        }

        val dashboardBudgetListFragmentTapped: Button = findViewById(R.id.dashboard_budgetListTab)
        val dashboardHistoryFragmentTapped: Button = findViewById(R.id.dashboard_historyTabButton)
        dashboardBudgetListFragmentTapped.setOnClickListener {
            // Switch to Fragment 1 = Budget List Activity Pane
            dashboardBudgetListFragmentTapped.typeface = Typeface.DEFAULT_BOLD
            dashboardHistoryFragmentTapped.typeface = Typeface.DEFAULT
            val firstFragment = BudgetListFragment() // get the Fragment Instance
            val manager = supportFragmentManager // Get the Support Fragment manager Instance
            val transactionManager =
                manager.beginTransaction() // Begin the Fragment Transaction using Fragment Manager

            // Replace Fragment in the Container and Finish Transaction
            transactionManager.replace(R.id.dashboardMainFragment, firstFragment)
            transactionManager.addToBackStack(null)
            transactionManager.commit()


        }
        dashboardHistoryFragmentTapped.setOnClickListener {
            // Switch to Fragment 2 = History Activity Pane
            dashboardHistoryFragmentTapped.typeface = Typeface.DEFAULT_BOLD
            dashboardBudgetListFragmentTapped.typeface = Typeface.DEFAULT
            val secondFragment = HistoryFragment() // get the Fragment Instance
            val manager = supportFragmentManager // Get the Support Fragment manager Instance
            val transactionManager =
                manager.beginTransaction() // Begin the Fragment Transaction using Fragment Manager

            // Replace Fragment in the Container and Finish Transaction
            transactionManager.replace(R.id.dashboardMainFragment, secondFragment)
            transactionManager.addToBackStack(null)
            transactionManager.commit()


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
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
    }


    fun showAddMoneyAlert(view: View) { // avoid warning error that is never used, it is actually used.
        var dashboardUserMoneyBalanceText: TextView = findViewById(R.id.dashboard_userMoneyBalance)
        val inflater = layoutInflater
        val inflaterView = inflater.inflate(R.layout.addmoney_dialog, null)

        val inputMoneyFieldText = inflaterView.findViewById(R.id.inputMoneyField) as EditText

        val addMoneyAlertDialog = AlertDialog.Builder(this)
        addMoneyAlertDialog.setTitle("Add Money Balance")
        addMoneyAlertDialog.setIcon(R.mipmap.ic_launcher)
        addMoneyAlertDialog.setView(inflaterView) // This set Custom XML in Alert Dialog
        addMoneyAlertDialog.setCancelable(false) // prevent cancel on outside touch of dialog
        addMoneyAlertDialog.create()
        addMoneyAlertDialog.setNegativeButton("Cancel") { _, _ ->

            return@setNegativeButton
        }
        addMoneyAlertDialog.setPositiveButton("Add") { _, _ ->
            try {
                val dashboardUserMoneyBalanceTextString =
                    dashboardUserMoneyBalanceText.text.toString().toDouble()
                val inputtedMoney = inputMoneyFieldText.text.toString().toDouble()
                val sumOfMoneyBalance =
                    (dashboardUserMoneyBalanceTextString + inputtedMoney).toString()
                databaseHelper.addMoney(formatDecimal(sumOfMoneyBalance).toString())
                dashboardUserMoneyBalanceText.text = formatDecimal(sumOfMoneyBalance)
            } catch (error: Exception) {
                when (error) { // This is Kotlin's multi-catch handling
                    is NullPointerException, is NumberFormatException, is FormatException -> {
                        val numberFormatExceptionToast =
                            Toast.makeText(
                                this@DashboardActivity,
                                "No money inputted",
                                Toast.LENGTH_SHORT
                            )
                        numberFormatExceptionToast.setGravity(Gravity.CENTER, 0, 0)
                        numberFormatExceptionToast.show()
                        return@setPositiveButton
                    }
                    else -> {
                        throw error
                    }

                }

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