package com.craftrom.manager.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.R

class WebViewActivity : AppCompatActivity() {
    lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        webView = findViewById(R.id.webView)
        val link: String = intent.getStringExtra("link").toString()

        webView.loadUrl(link)
        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        webView.canGoBack()
        webView.setOnKeyListener(View.OnKeyListener{ _, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_BACK
                && event.action == MotionEvent.ACTION_UP
                && webView.canGoBack()){
                webView.goBack()
                return@OnKeyListener true
            }
            false
        })

    }
}