package com.example.habittracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity)

        /*
        if (alreadyloggedin()){
            val intent = Intent(this,afterlogin::class.java)
            startActivity(intent)
        }

        val loginbtn = findViewById<Button>(R.id.loginbtn)
        loginbtn.setOnClickListener {

            val sharedPreferences = this.getSharedPreferences("MySharedPrefference", Context.MODE_PRIVATE)
                ?: return@setOnClickListener
            with(sharedPreferences.edit()){
                putBoolean("savedText",true)
                apply()
            }

            val intent = Intent(this,afterlogin::class.java)
            startActivity(intent)
        }

         */




    }
    /*
    private fun alreadyloggedin(): Boolean {
        val sharedPreferences = getSharedPreferences("MySharedPrefference", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("savedText", false)

    }

    */

}