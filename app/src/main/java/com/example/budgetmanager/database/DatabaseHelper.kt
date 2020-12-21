package com.example.budgetmanager.database

import android.content.ClipDescription
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.BUDGET_DESCRIPTION
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.BUDGET_MONEY
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.BUDGET_STATUS
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.BUDGET_TABLE
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.BUDGET_TITLE
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.NAME_COLUMN
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.TABLE_NAME
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.USER_MONEY
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.USER_NAME
import com.example.budgetmanager.database.DatabaseContainer.PersonTable.Companion.USER_TABLE

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        var personTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COLUMN + " TEXT" + ")"
        db!!.execSQL(personTable)
        personTable = "CREATE TABLE " + USER_TABLE + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_NAME + " TEXT, " +
                USER_MONEY + " TEXT" + ")"
        db!!.execSQL(personTable)
        personTable = "CREATE TABLE " + BUDGET_TABLE + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BUDGET_TITLE + " TEXT, " +
                BUDGET_MONEY + " TEXT, " +
                BUDGET_DESCRIPTION + " TEXT, " +
                BUDGET_STATUS + " TEXT" + ")"
        db!!.execSQL(personTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $USER_TABLE")
    }

    fun insertData(name: String) : Boolean{
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME_COLUMN, name)
        val insert_data = db.insert(TABLE_NAME, null, contentValues)
        db.close()

        return !insert_data.equals(-1)
    }

    fun readData(): Cursor {
        val db: SQLiteDatabase = this.writableDatabase
        val read: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY NAME ASC", null)
        return read
    }

    fun userAccount(): Cursor {
        val db: SQLiteDatabase = this.writableDatabase
        var read: Cursor = db.rawQuery("SELECT * FROM $USER_TABLE", null)
        if (read.count == 0){
            val contentValues = ContentValues()
            contentValues.put(USER_NAME, "user1234")
            contentValues.put(USER_MONEY, "0")
            db.insert(USER_TABLE, null, contentValues)
            read = db.rawQuery("SELECT * FROM $USER_TABLE", null)
        }
        return read
    }

    fun addMoney(userMoney: String): Boolean{
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(USER_MONEY, userMoney)
        val update_data = db.update(USER_TABLE, contentValues, "${BaseColumns._ID}=?", arrayOf("1"))
        db.close()

        return !update_data.equals(-1)
    }

    fun createBudget(budgetTitle: String, budgetMoney: String, budgetDescription: String){
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(BUDGET_TITLE, budgetTitle)
        contentValues.put(BUDGET_MONEY, budgetMoney)
        contentValues.put(BUDGET_DESCRIPTION, budgetDescription)
        contentValues.put(BUDGET_STATUS, "pending")
        db.insert(BUDGET_TABLE, null, contentValues)
        db.close()
    }

    fun readBudget(): Cursor {
        val db: SQLiteDatabase = this.writableDatabase
        val read: Cursor = db.rawQuery("SELECT * FROM $BUDGET_TABLE ORDER BY $BUDGET_TITLE ASC", null)
        return read
    }

    companion object{
        private const val DATABASE_NAME = "Person.db"
        private const val DATABASE_VERSION = 1
    }

}