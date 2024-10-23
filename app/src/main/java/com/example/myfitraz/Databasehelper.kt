package com.example.myfitraz

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 2) {

    companion object {
        const val DATABASE_NAME = "User.db"

        // Users table constants
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "ID"
        const val COL_USER_EMAIL = "EMAIL"
        const val COL_USER_USERNAME = "USERNAME"
        const val COL_USER_PASSWORD = "PASSWORD"

        // User metrics table constants
        const val TABLE_USER_METRICS = "user_metrics"
        const val COL_METRIC_ID = "ID"
        const val COL_METRIC_USER_ID = "USER_ID"
        const val COL_METRIC_HEIGHT = "HEIGHT"
        const val COL_METRIC_WEIGHT = "WEIGHT"
        const val COL_METRIC_BMI = "BMI"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper", "Creating tables...")

        // Create Users Table
        db.execSQL("CREATE TABLE $TABLE_USERS (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, USERNAME TEXT, PASSWORD TEXT)")

        // Create UserMetrics Table
        db.execSQL("CREATE TABLE $TABLE_USER_METRICS (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, HEIGHT REAL, WEIGHT REAL, BMI REAL, FOREIGN KEY(USER_ID) REFERENCES $TABLE_USERS(ID))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_METRICS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Insert user method
    fun insertUser(email: String, username: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_USER_EMAIL, email)
            put(COL_USER_USERNAME, username)
            put(COL_USER_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, contentValues)
        return result != -1L
    }

    // Insert user metrics method
    fun insertUserMetrics(userId: Int, height: Double, weight: Double, bmi: Double): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_METRIC_USER_ID, userId)
            put(COL_METRIC_HEIGHT, height)
            put(COL_METRIC_WEIGHT, weight)
            put(COL_METRIC_BMI, bmi)
        }
        val result = db.insert(TABLE_USER_METRICS, null, contentValues)
        return result != -1L
    }

    // Check if email already exists
    fun checkEmail(email: String): Boolean {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE EMAIL = ?", arrayOf(email))
        return cursor.count > 0
    }

    // Get user ID by email
    fun getUserIdByEmail(email: String): Int? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT ID FROM $TABLE_USERS WHERE EMAIL = ?", arrayOf(email))
        return if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex(COL_USER_ID))
        } else {
            null
        }
    }

fun getUserMetrics(): Cursor {
        val db = this.readableDatabase
        return db.query("user_metrics", arrayOf("weight", "height", "bmi"),
            null, null, null, null, null)
    }

}
