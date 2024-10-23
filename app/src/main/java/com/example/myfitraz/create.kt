package com.example.myfitraz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class create : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        val emailEditText: EditText = findViewById(R.id.email)
        val nameEditText: EditText = findViewById(R.id.name)
        val passwordEditText: EditText = findViewById(R.id.makepass)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmpass)
        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val name = nameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validation logic for registration
            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                val dbHelper = DatabaseHelper(this)

                // Check if the email already exists in the database
                if (dbHelper.checkEmail(email)) {
                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // Insert the new user with a default height, weight, and BMI
                    val isInserted = dbHelper.insertUser(email, name, password)

                    if (isInserted) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

                        // After user creation, optionally you can redirect to a dashboard activity
                        // Intent(this, DashboardActivity::class.java).also {
                        //     startActivity(it)
                        //     finish() // Close the current activity
                        // }
                    } else {
                        Toast.makeText(this, "Error occurred while creating account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
