package com.craftrom.manager.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val link: String
        link = intent.getStringExtra("link").toString()

        webView.loadUrl(link)
        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        webView.canGoBack()
        webView.setOnKeyListener(View.OnKeyListener{ v, keyCode, event ->
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