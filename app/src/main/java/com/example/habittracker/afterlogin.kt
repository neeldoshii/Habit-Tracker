package com.example.habittracker

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class afterlogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_afterlogin)
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

         */
        val sharedPreferences = getSharedPreferences("MySharedPrefference", Context.MODE_PRIVATE)
        val savedText = sharedPreferences.getBoolean("savedText", false)
        println(savedText)
    }
}