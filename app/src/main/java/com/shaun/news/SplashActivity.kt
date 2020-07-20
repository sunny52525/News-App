package com.shaun.news

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashActivity : AppCompatActivity(), GetRawData.OnDownloadComplete {
    private val SPLASH_TIME_OUT = 5000L   //Time out for PreDownload
    var rawdataa =""
    var started = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_splash_screen)

        val getRawData = GetRawData(this)
        getRawData.downloadRawData(
            "https://newsapi.org/v2/top-headlines?q=india&sortBy=published&pageSize=100&apiKey=c5505b6406384fe2b1060c7dd66e957c",
            2
        )
        Handler().postDelayed({
            StartMain(true)
        }, SPLASH_TIME_OUT)
    }

    override fun onDownloadComplete(data: Pair<String, Int>, status: DownloadStatus, id: Int) {
        rawdataa = data.first
        if (!animation_view.isAnimating) {
            StartMain(false)
        } else {
            Handler().postDelayed({
                StartMain(false)
            }, 3000)
        }

    }

    private fun StartMain(toast: Boolean) {
        if (!started) {
            started = true
            val i = Intent(
                this,
                MainActivity::class.java
            )
            if (toast)
                Toast.makeText(this, "Slow Connection Detected", Toast.LENGTH_SHORT).show()
            Log.d("Splash", rawdataa)
            i.putExtra("RAW", rawdataa)
            startActivity(i)
            finish()
        }
    }
}