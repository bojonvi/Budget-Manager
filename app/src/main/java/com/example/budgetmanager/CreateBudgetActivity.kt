package com.example.budgetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class CreateBudgetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_budget_activity)

        // BUG IN LINE 15 - Caused by: java.lang.NullPointerException: findViewById(R.id.dashboard_userMoneyBalance) must not be null
        // logic ko: kung ano ang text string sa Available Balance, kunin nya at i-load sa Create B udget Activity
        val dashboard_userMoneyBalanceLoad: TextView = findViewById(R.id.dashboard_userMoneyBalance)
        val createBudgetPreviewDashboardAvailableBalanceLoad: TextView =
            findViewById(R.id.createBudgetPreviewDashboardAvailableBalance)
        // Load Dashboard Money Available balance to the Create budget Activity Window
        createBudgetPreviewDashboardAvailableBalanceLoad.text = dashboard_userMoneyBalanceLoad.toString()

    }
}