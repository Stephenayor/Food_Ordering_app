package com.example.yummy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.yummy.MainActivity
import com.example.yummy.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
                val startMainActivityIntent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(startMainActivityIntent)
                finish()
            }, 3000)
    }
}