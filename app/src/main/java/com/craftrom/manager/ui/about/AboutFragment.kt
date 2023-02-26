package com.craftrom.manager.ui.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.craftrom.manager.BuildConfig
import com.craftrom.manager.R
import com.craftrom.manager.core.ServiceContext
import com.craftrom.manager.ui.view.ItemWebViewActivity

class AboutFragment : Fragment(), View.OnClickListener {
    private lateinit var imageView: ImageView
    private lateinit var ghimg: ImageView
    private lateinit var webimg: ImageView
    private lateinit var versionApp: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_about, container, false)
        imageView = root.findViewById(R.id.tg_link)
        imageView.setOnClickListener(this)
        ghimg = root.findViewById(R.id.gh_link)
        ghimg.setOnClickListener(this)
        webimg = root.findViewById(R.id.web_link)
        webimg.setOnClickListener(this)

        versionApp = root.findViewById(R.id.version)

        val versionName = BuildConfig.VERSION_NAME
        versionApp.text = versionName

        return root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tg_link -> openTG()
            R.id.gh_link -> openGH()
            R.id.web_link -> openWeb()
        }
    }

    private fun openTG() {
        val uri = Uri.parse("https://t.me/craftrom")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("org.telegram.messenger")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // The installed Telegram app doesn't support opening links via WebView, try using org.telegram.messenger.web
            intent.setPackage("org.telegram.messenger.web")
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Prompt user to install Telegram or a compatible app
                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=org.telegram.messenger")
                }
                startActivity(installIntent)
            }
        }
    }

    private fun openGH() {
        val uri = Uri.parse("https://github.com/CraftRom")
        val intent = Intent(ServiceContext.context, ItemWebViewActivity::class.java).apply {
            putExtra("feedItemUrl", uri)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun openWeb() {
        val uri = Uri.parse("https://www.craft-rom.pp.ua/")
        val intent = Intent(ServiceContext.context, ItemWebViewActivity::class.java).apply {
            putExtra("feedItemUrl", uri)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}
