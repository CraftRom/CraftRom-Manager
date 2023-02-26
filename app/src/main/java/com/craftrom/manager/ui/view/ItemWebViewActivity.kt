package com.craftrom.manager.ui.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
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

    private var pageTitle: String? = null

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
        pageTitle = intent.getStringExtra("pageTitle") ?: pageUrl // сохраняем название страницы, если оно есть, иначе сохраняем URL страницы
        setPageTitle("Just a moment...")
    }

    private fun setPageTitle(title: String) {
        supportActionBar?.apply {
            val titleTextView = findViewById<TextView>(R.id.toolbar_title)
            val subtitleTextView = findViewById<TextView>(R.id.toolbar_subtitle)


            titleTextView.text = title
            subtitleTextView.text = pageUrl
            titleTextView.maxLines = 1
            subtitleTextView.maxLines = 1

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView = findViewById(R.id.webview_feed_item)
        webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.minimumFontSize = MIN_FONT_SIZE
        val statusIcon = findViewById<ImageView>(R.id.status_icon)
        statusIcon.visibility = View.INVISIBLE

        webView.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                setPageTitle(title ?: pageUrl)
            }
        }

        // Добавляем проверку безопасного соединения
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    return false
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (webView.url?.startsWith("https://") == true) {
                    statusIcon.visibility = View.VISIBLE
                    statusIcon.setImageResource(R.drawable.outline_https_24)
                } else {
                    statusIcon.visibility = View.VISIBLE
                    statusIcon.setImageResource(R.drawable.outline_lock_open_24)
                }
            }
        }

        if (!CookieManager.getInstance().acceptThirdPartyCookies(webView)) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
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