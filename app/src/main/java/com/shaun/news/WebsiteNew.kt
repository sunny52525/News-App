package com.shaun.news

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.webview.*


class WebViewSampleActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview)
        setTheme(android.R.style.ThemeOverlay_Material_Dark_ActionBar)
        val intent = intent
        var link = intent.extras?.getString("data")
        var correctLink = ""
        if (link?.get(4)!! != 's') {
            correctLink = link.substring(0, 4) + 's' + link.substring(4, link.length)

        } else correctLink = link

        Log.d("WEBVIEW", correctLink)

        webView1.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                view.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                Log.d("Websview","Page Loaded")
                view.visibility =View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }

        }
        webView1.settings.javaScriptEnabled = false

        val settings = webView1.settings
        settings.domStorageEnabled = true

        webView1.loadUrl(correctLink)

    }


    override fun onBackPressed() {
        if (webView1.canGoBack()) {
            webView1.goBack()
        } else {
            super.onBackPressed()
        }
    }
}