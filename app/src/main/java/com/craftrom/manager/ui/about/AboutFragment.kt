package com.craftrom.manager.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.craftrom.manager.BuildConfig
import com.craftrom.manager.R
import com.google.android.material.bottomsheet.BottomSheetDialog


class AboutFragment: Fragment(), View.OnClickListener {
    private lateinit var imageView: ImageView
    private lateinit var ghimg: ImageView
    private lateinit var webimg: ImageView
    lateinit var versionApp: TextView

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
    private fun openTG() {
        val uri = Uri.parse("https://t.me/craftrom")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("org.telegram.messenger.web")
        startActivity(intent)
    }

    private fun openGH() {
        val uri = Uri.parse("https://github.com/CraftRom")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.github.android")
        startActivity(intent)
    }
    private fun openWeb() {
        val uri = Uri.parse("https://www.craft-rom.pp.ua/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tg_link -> openTG()
            R.id.gh_link -> openGH()
            R.id.web_link -> openWeb()
//            R.id.ll_osl ->  startActivity(Intent(context, OSLActivity::class.java))
        }
    }

}