package com.shaun.news

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


 class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            val i = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(i)
            finish()
        }, SPLASH_TIME_OUT)
    }
}