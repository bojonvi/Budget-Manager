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
import androidx.core.content.ContextCompat
import com.example.budgetmanager.R.drawable.ic_add_dialog
import com.example.budgetmanager.R.drawable.ic_cancel_dialog
import com.example.budgetmanager.database.DatabaseHelper
import java.text.DecimalFormat

class DashboardActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private var backToast: Toast? = null

    lateinit var accountTextView: TextView
    lateinit var databaseHelper: DatabaseHelper
    lateinit var userMoney: String

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
            userMoney = userData.getString((2))
        }
        accountTextView.text = formatDecimal(userMoney)


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


    fun showAddMoneyAlert(@Suppress("UNUSED_PARAMETER") view: View) {
        /* > avoid warning error that the (view: View) is never used, it is actually used to
            show Alert Dialog Box.
        > dashboardUserMoneyBalanceText, should be [var] because the numbers are always changing
          by the user's input and action such as adding and revoking. */
        var dashboardUserMoneyBalanceText: TextView = findViewById(R.id.dashboard_userMoneyBalance)
        val inflater = layoutInflater
        val inflaterView = inflater.inflate(R.layout.addmoney_dialog, null)

        val inputMoneyFieldText = inflaterView.findViewById(R.id.inputMoneyField) as EditText
        
        val addMoneyAlertDialog = AlertDialog.Builder(this)
        addMoneyAlertDialog.setTitle("Add Money to Account Balance")
        addMoneyAlertDialog.setNegativeButtonIcon(ContextCompat.getDrawable(this, ic_cancel_dialog))
        addMoneyAlertDialog.setPositiveButtonIcon(ContextCompat.getDrawable(this, ic_add_dialog))
        addMoneyAlertDialog.setIcon(R.mipmap.ic_launcher)
        addMoneyAlertDialog.setView(inflaterView) // This set Custom XML in Alert Dialog
        addMoneyAlertDialog.setCancelable(false) // prevent cancel on outside touch of dialog
        addMoneyAlertDialog.create()
        addMoneyAlertDialog.setNegativeButton("Cancel") { _, _ -> return@setNegativeButton }
        addMoneyAlertDialog.setPositiveButton("Add") { _, _ ->
            try {
                val inputtedMoney = "%.2f".format(inputMoneyFieldText.text.toString().toFloat())
                val sumOfMoneyBalance =
                    (userMoney.toFloat() + inputtedMoney.toFloat())
                // If the user's inputted money is over 100,000 then warn users
                if (inputtedMoney.toFloat() > 100000) {
                    val inputMoneyWarningToastMessage = Toast.makeText(
                        this,
                        "The Inputted money should not exceed more than 100,000PHP",
                        Toast.LENGTH_SHORT
                    )
                    inputMoneyWarningToastMessage.setGravity(Gravity.CENTER, 0, 0)
                    inputMoneyWarningToastMessage.show()

                    // Else if the Total amount of Money Balance in Dashboard is over 100,000, then
                } else if (sumOfMoneyBalance > 100000) {
                    val totalSumBalanceWarningToastMessage = Toast.makeText(
                        this,
                        "The Money Balance should not exceed mroe than 100,000PHP",
                        Toast.LENGTH_SHORT
                    )
                    totalSumBalanceWarningToastMessage.setGravity(Gravity.CENTER, 0, 0)
                    totalSumBalanceWarningToastMessage.show()

                    // Else, if everything is fine & Dashboard money balance
                    // and inputted money is not over 100,000PHP, then add money
                } else {
                    userMoney = "%.2f".format(sumOfMoneyBalance)
                    databaseHelper.addMoney(userMoney)
                    dashboardUserMoneyBalanceText.text = formatDecimal(sumOfMoneyBalance.toString())
                }

            } catch (error: Exception) {
                when (error) { // This is Kotlin's multi-catch handling
                    is NullPointerException, is NumberFormatException, is FormatException -> {
                        val numberFormatExceptionToast =
                            Toast.makeText(
                                this@DashboardActivity,
                                "No money balance inputted.",
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
        if (userMoney.toFloat() >= 100000) {
            Toast.makeText(
                this,
                "The allowance money balance is up to 100,000PHP only",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val addMoneyDialogBox = addMoneyAlertDialog.create()
            addMoneyDialogBox.show()
        }
    }

    private fun formatDecimal(value: String?): String? {
        val df = DecimalFormat("#,###,##0.00")
        return df.format(java.lang.Double.valueOf(value!!))
    }
}