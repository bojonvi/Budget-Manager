package com.example.budgetmanager.database

import android.provider.BaseColumns

object DatabaseContainer {
    class PersonTable: BaseColumns{
        companion object{
            val TABLE_NAME = "Person_table"
            val NAME_COLUMN = "NAME"
            val USER_TABLE = "User_table"
            val USER_NAME = "USER_NAME"
            val USER_MONEY = "USER_MONEY"
            val BUDGET_TABLE = "Budget_table"
            val BUDGET_TITLE = "BUDGET_TITLE"
            val BUDGET_DESCRIPTION = "BUDGET_DESCRIPTION"
            val BUDGET_MONEY = "BUDGET_MONEY"
            val BUDGET_STATUS = "BUDGET_STATUS"
        }
    }
}