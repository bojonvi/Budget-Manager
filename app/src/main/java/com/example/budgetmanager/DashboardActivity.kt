package com.example.budgetmanager

import android.content.Intent
import android.database.Cursor
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
    lateinit var availableMoney: String
    lateinit var userData: Cursor
    lateinit var budgetData: Cursor

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

    override fun onStart() {
        super.onStart()

        // Loads Budget List and User Account
        budgetListShow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)


        // Variables
        databaseHelper = DatabaseHelper(this)
        accountTextView = findViewById(R.id.dashboard_userMoneyBalance)


        // Buttons
        val dashboardCreateBudgetActivityTapped: Button =
            findViewById(R.id.dashboard_createBudgetButton)
        dashboardCreateBudgetActivityTapped.setOnClickListener {
            val activity = Intent(this, CreateBudgetActivity::class.java)
            activity.putExtra("availableMoney", availableMoney)
            startActivity(activity)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val dashboardHelpButtonTapped: ImageView = findViewById(R.id.dashboard_helpButton)
        dashboardHelpButtonTapped.setOnClickListener {
            val helpPopupMenu = PopupMenu(this@DashboardActivity, dashboardHelpButtonTapped)
            helpPopupMenu.menuInflater.inflate(R.menu.menu_popup, helpPopupMenu.menu)
            helpPopupMenu.setOnMenuItemClickListener(object : OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item!!.itemId) {
                        R.id.howToUseAppOption -> goToHowToUseApp()
                        R.id.privacyPolicyOption -> goToPrivacyPolicy()
                        R.id.settingsOption -> goToSettingsActivity()
                    }
                    return true
                }

                private fun goToPrivacyPolicy() {
                    startActivity(Intent(this@DashboardActivity, PrivacyPolicy::class.java))
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
                }

                private fun goToHowToUseApp() {
//                    val howToUseAppIntent =
//                        Intent(this@DashboardActivity, SettingsActivity::class.java)
//                    startActivity(howToUseAppIntent)
//                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
                    val maintenanceInformationAlertDialog =
                        AlertDialog.Builder(this@DashboardActivity)
                    maintenanceInformationAlertDialog.setTitle("Not yet accessible")
                    maintenanceInformationAlertDialog.setMessage("Instructions on How to use iBudget will be available soon.")
                    maintenanceInformationAlertDialog.setCancelable(true) // can cancel on outside touch of dialog
                    maintenanceInformationAlertDialog.setPositiveButton("Okay") { _, _ -> return@setPositiveButton }
                    val maintenanceInformationAlertDialogShow =
                        maintenanceInformationAlertDialog.create()
                    maintenanceInformationAlertDialogShow.show()
                }

            }) // helpPopupMenu.setOnMenuItemClickListener({ }) Code
            helpPopupMenu.show()
        } // dashboardHelpButtonTapped.setOnClickListener() Code

    } // fun onCreate() Code

    private fun goToSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
    } // fun goToSettingsActivity() Code

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
                        "The Money Balance should not exceed more than 100,000PHP",
                        Toast.LENGTH_SHORT
                    )
                    totalSumBalanceWarningToastMessage.setGravity(Gravity.CENTER, 0, 0)
                    totalSumBalanceWarningToastMessage.show()

                    // Else, if everything is fine & Dashboard money balance
                    // and inputted money is not over 100,000PHP, then add money
                } else {
                    userMoney = "%.2f".format(sumOfMoneyBalance)
                    databaseHelper.updateMoney(userMoney)
                    availableMoney = (availableMoney.toFloat() + inputtedMoney.toFloat()).toString()
                    dashboardUserMoneyBalanceText.text = formatDecimal(availableMoney)
                }

                // https://youtu.be/Vy_4sZ6JVHM 3:42 duration .

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
                    } // is NullPointerException, is NumberFormatException, is FormatException  Code
                    else -> {
                        throw error
                    }
                } // when (error) Code
            } //  catch (error: Exception) Code
        } //  addMoneyAlertDialog.setPositiveButton("Add") Code
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
    } // fun showAddMoneyAlert() Code

    private fun formatDecimal(value: String?): String? {
        val df = DecimalFormat("#,###,##0.00")
        return df.format(java.lang.Double.valueOf(value!!))
    } // fun formatDecimal() Code

    private fun userDataLoad(){
        userData = databaseHelper.userAccount()
        while (userData.moveToNext()) {
            userMoney = userData.getString((2))
        }
        availableMoney = (userMoney.toFloat()).toString()
    }

    private fun budgetListShow(){
        // Layout views
        val dashboard_MainFrameLinearLayout = findViewById<LinearLayout>(R.id.dashboard_MainFrameLinearLayout)

        // Resets the contents of the main layout
        if (dashboard_MainFrameLinearLayout != null){
            dashboard_MainFrameLinearLayout.removeAllViews()
        }

        // Loads the data (USER and BUDGET) from database
        userDataLoad()
        budgetData = databaseHelper.readBudget()
        while (budgetData.moveToNext()) {
            // Budget Data
            val budgetID = budgetData.getString(0)
            val budgetTitle = budgetData.getString(1)
            val budgetMoney = budgetData.getString(2)
            val budgetDescription = budgetData.getString(3)
            availableMoney =
                (availableMoney.toFloat() - budgetMoney.toFloat()).toString()

            // Initialize Card Layout Views
            val layoutBudgetCard = LinearLayout(this)
            layoutBudgetCard.orientation = LinearLayout.HORIZONTAL
            val budgetCardParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            budgetCardParams.setMargins(20,20,20,20)
            layoutBudgetCard.layoutParams = budgetCardParams
            val layoutInfoCard = LinearLayout(this)
            layoutInfoCard.orientation = LinearLayout.VERTICAL
            layoutInfoCard.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .7f)
            val layoutActionCard = LinearLayout(this)
            layoutActionCard.orientation = LinearLayout.VERTICAL
            layoutActionCard.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .3f)

            // Initialize InfoCard Content Views
            val budgetTitleTextView = TextView(this)
            budgetTitleTextView.text = "Title: " + budgetTitle
            budgetTitleTextView.textSize = 20f
            val budgetMoneyTextView = TextView(this)
            budgetMoneyTextView.text = "PHP " + formatDecimal(budgetMoney)
            budgetMoneyTextView.textSize = 15f

            // Initialize ActionCard Content Views
            val budgetFinishButton = Button(this)
            budgetFinishButton.text = "Finish"
            budgetFinishButton.textSize = 15f
            budgetFinishButton.setOnClickListener{
                databaseHelper.updateBudget(budgetID, "finish")
                databaseHelper.updateMoney((userMoney.toFloat() - budgetMoney.toFloat()).toString())
                budgetListShow()
            }
            val budgetRevokeButton = Button(this)
            budgetRevokeButton.text = "Revoke"
            budgetRevokeButton.textSize = 15f
            budgetRevokeButton.setOnClickListener{
                databaseHelper.updateBudget(budgetID, "revoked")
                budgetListShow()
            }

            // Add content views to layout
            layoutInfoCard.addView(budgetTitleTextView)
            layoutInfoCard.addView(budgetMoneyTextView)
            layoutActionCard.addView(budgetFinishButton)
            layoutActionCard.addView(budgetRevokeButton)

            // Creates and add Budget Card to main layout
            layoutBudgetCard.addView(layoutInfoCard)
            layoutBudgetCard.addView(layoutActionCard)
            dashboard_MainFrameLinearLayout.addView(layoutBudgetCard)
        }

        accountTextView.text = formatDecimal(availableMoney)
    }
}