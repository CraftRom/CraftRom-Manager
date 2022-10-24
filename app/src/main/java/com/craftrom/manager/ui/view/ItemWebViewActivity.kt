package com.craftrom.manager.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.R
import org.koin.android.ext.android.inject


class ItemWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var webSettings: WebSettings
    private lateinit var pageUrl : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_web_view)
        setPageUrl()
        configureWebView()
        loadPage()
    }

    private fun setPageUrl() {
        val intent = this.intent
        if (intent != null) {
            pageUrl = intent.getStringExtra("feedItemUrl")!!
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView = findViewById(R.id.webview_feed_item)
        webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true

        webSettings.minimumFontSize = MIN_FONT_SIZE

        if (!CookieManager.getInstance().acceptCookie()) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = FeedItemWebViewClient(this)
    }

    private fun loadPage() {
        try {
            webView.loadUrl(pageUrl)
        } catch (e : Exception ) {
            Log.d("loadError", e.message!!)
        }
    }

    companion object {
        var MIN_FONT_SIZE: Int = 14
    }
}