package com.craftrom.manager.ui.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.craftrom.manager.R
import com.craftrom.manager.SplashActivity
import com.craftrom.manager.databinding.ActivityItemWebViewBinding

class ItemWebViewActivity : SplashActivity() {

    private lateinit var menu: Menu

    private lateinit var webView: WebView
    private lateinit var webSettings: WebSettings
    private lateinit var pageUrl: String
    private lateinit var binding: ActivityItemWebViewBinding

    override fun showMainUI(savedInstanceState: Bundle?) {
        binding = ActivityItemWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Включение кнопки "Назад"
        setPageUrl()
        configureWebView()
        loadPage()
    }

    private fun setPageUrl() {
        val intent = intent
        pageUrl = intent.getStringExtra("feedItemUrl") ?: ""
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView = findViewById(R.id.webview_feed_item)
        webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.minimumFontSize = MIN_FONT_SIZE

        if (!CookieManager.getInstance().acceptThirdPartyCookies(webView)) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = object : WebViewClient() {

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                showError()
            }
        }
    }

    private fun loadPage() {
        try {
            webView.loadUrl(pageUrl)
        } catch (e: Exception) {
            Log.d("loadError", e.message ?: "")
        }
    }
    private fun showError() {
        Toast.makeText(this, "An error occurred while loading the page", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_webview, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_copy_link -> {
                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("feedItemUrl", pageUrl)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_open_in_browser -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl))
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack() // Возврат к предыдущей странице
        } else {
            super.onBackPressed()
        }
    }


    companion object {
        const val MIN_FONT_SIZE = 14
    }
}