package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity)
        // TODO DEPENDENCY INJECT
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null){
            val intent = Intent(this,HomescreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}