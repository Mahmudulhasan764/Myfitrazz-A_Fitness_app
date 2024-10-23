package com.example.myfitraz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Loginpage", "Loginpage created")
        val startButton: Button = findViewById(R.id.button)
        startButton.setOnClickListener {
            // Intent to move from MainActivity to Loginpage
            Toast.makeText(this, "Start button clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Loginpage::class.java)
            startActivity(intent)
        }




    }
}