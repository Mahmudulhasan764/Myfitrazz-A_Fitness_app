package com.example.myfitraz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Loginpage : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        // Initialize the database helper
        dbHelper = DatabaseHelper(this)

        val emailEditText: EditText = findViewById(R.id.username) // Assuming this is for email
        val passwordEditText: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Check credentials in the database
                val user = checkCredentials(email, password)
                if (user != null) {
                    // User found, pass data to Dash activity
                    val intent = Intent(this, Dash::class.java)
                    intent.putExtra("user_email", user.first) // Passing the email
                    intent.putExtra("user_username", user.second) // Passing the username
                    startActivity(intent)
                    finish() // Optionally close the login activity
                } else {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val createButton: Button = findViewById(R.id.CreateButton)
        createButton.setOnClickListener {
            // Intent to move to the registration page
            val intent = Intent(this, create::class.java)
            startActivity(intent)
        }
    }

    // Method to check credentials and return user details
    private fun checkCredentials(email: String, password: String): Pair<String, String>? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT EMAIL, USERNAME FROM ${DatabaseHelper.TABLE_USERS} WHERE EMAIL = ? AND PASSWORD = ?", arrayOf(email, password))

        return if (cursor.moveToFirst()) {
            val userEmail = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_EMAIL))
            val username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_USERNAME))
            cursor.close()
            Pair(userEmail, username) // Return email and username as a Pair
        } else {
            cursor.close()
            null // Return null if no user found
        }
    }
}
