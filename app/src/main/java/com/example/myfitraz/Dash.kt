package com.example.myfitraz

import android.database.Cursor // Make sure to import this
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ContentValues
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.text.DecimalFormat

class Dash : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)

        // Initialize the database helper
        dbHelper = DatabaseHelper(this)

        // Get user email and username from the intent
        val userEmail = intent.getStringExtra("user_email")
        val userUsername = intent.getStringExtra("user_username")

        if (userEmail != null) {
            // Check if height, weight, and BMI are present for this user
            val missingData = checkUserData(userEmail)

            if (missingData) {
                // Show AlertDialog to prompt for missing data
                showDataInputDialog(userEmail)
            } else {
                // Show a welcome message if data is complete
                Toast.makeText(this, "Welcome, $userUsername! Your data is complete.", Toast.LENGTH_LONG).show()
            }
        }

        // Display user info on the dashboard
       val welcomeMessage: TextView = findViewById(R.id.welcometext)
        welcomeMessage.text = "Welcome, $userUsername!"

        // Load metrics from the database
        displayUserMetrics()
    }

    // Method to check if user's height, weight, and BMI are present in the database
    private fun checkUserData(email: String): Boolean {
        val userId = dbHelper.getUserIdByEmail(email) ?: return true // Return true if user not found

        val db = dbHelper.readableDatabase
        val columns = arrayOf(
            DatabaseHelper.COL_METRIC_HEIGHT,
            DatabaseHelper.COL_METRIC_WEIGHT,
            DatabaseHelper.COL_METRIC_BMI
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_USER_METRICS,
            columns,
            "${DatabaseHelper.COL_METRIC_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, // groupBy
            null, // having
            null  // orderBy
        )

        return try {
            if (cursor.moveToFirst()) {
                val height = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_METRIC_HEIGHT))
                val weight = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_METRIC_WEIGHT))
                val bmi = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_METRIC_BMI))

                // Return true if any of these values are missing (i.e., 0)
                height == 0.0 || weight == 0.0 || bmi == 0.0
            } else {
                Log.w("Dash", "No record found for user ID: $userId")
                true
            }
        } catch (e: Exception) {
            Log.e("Dash", "Error querying user data: ${e.message}")
            true
        } finally {
            cursor.close()
        }
    }

    // Method to show a dialog for inputting height, weight, and BMI
    private fun showDataInputDialog(email: String) {
        val dialogView = layoutInflater.inflate(R.layout.activity_user_data, null)
        val heightInput: EditText = dialogView.findViewById(R.id.inputHeight)
        val weightInput: EditText = dialogView.findViewById(R.id.inputWeight)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter your height and weight")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val height = heightInput.text.toString().toDoubleOrNull() ?: 0.0
                val weight = weightInput.text.toString().toDoubleOrNull() ?: 0.0
                val bmi = if (height != 0.0) weight / ((height / 100) * (height / 100)) else 0.0

                val userId = dbHelper.getUserIdByEmail(email)

                if (userId != null && height != 0.0 && weight != 0.0) {
                    val success = dbHelper.insertUserMetrics(userId, height, weight, bmi)
                    if (success) {
                        Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid input.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Method to display user metrics
    private fun displayUserMetrics() {
        val cursor: Cursor = dbHelper.getUserMetrics() // Changed to dbHelper

        if (cursor.moveToFirst()) {
            val weightIndex = cursor.getColumnIndex(DatabaseHelper.COL_METRIC_WEIGHT)
            val heightIndex = cursor.getColumnIndex(DatabaseHelper.COL_METRIC_HEIGHT)
            val bmiIndex = cursor.getColumnIndex(DatabaseHelper.COL_METRIC_BMI)

            // Assuming only one row is present for the logged-in user
            val weight = cursor.getDouble(weightIndex)
            val height = cursor.getDouble(heightIndex)
            val bmi = cursor.getDouble(bmiIndex)

            val decimalFormat = DecimalFormat("#.##") // This formats the BMI to 2 decimal places

            // Step 2: Format the BMI value
            val formattedBmi = decimalFormat.format(bmi) // Format the original BMI value

            // Find TextViews and update their text
            val weightTextView: TextView = findViewById(R.id.weightTextView)
            val heightTextView: TextView = findViewById(R.id.heightTextView)
            val bmiTextView: TextView = findViewById(R.id.bmiTextView)

            weightTextView.text = "Weight: $weight kg"
            heightTextView.text = "Height: $height cm"
            bmiTextView.text = "BMI: $formattedBmi"
        }
        cursor.close()
    }
}
