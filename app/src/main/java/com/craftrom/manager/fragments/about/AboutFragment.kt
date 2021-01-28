package com.craftrom.manager.fragments.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.craftrom.manager.BuildConfig
import com.craftrom.manager.R
import com.craftrom.manager.activities.OSLActivity

class AboutFragment: Fragment(), View.OnClickListener {

    private lateinit var aboutViewModel: AboutViewModel
    private lateinit var osl: LinearLayout
    private lateinit var imageView: ImageView
    private lateinit var ghimg: ImageView
    private lateinit var webimg: ImageView
    lateinit var versionApp: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        aboutViewModel =
            ViewModelProvider(this).get(AboutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_about, container, false)
        osl = root.findViewById(R.id.ll_osl)
        osl.setOnClickListener(this)
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
    fun openTG() {
        val uri = Uri.parse("https://t.me/craft_rom")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        //intent.setPackage("org.thunderdog.challegram");
        startActivity(intent)
    }

    fun openGH() {
        val uri = Uri.parse("https://github.com/CraftRom")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
    fun openWeb() {
        val uri = Uri.parse("https://www.craft-rom.ml/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


    override fun onClick(v: View) {
        val view = v.id
        when (view) {
            R.id.tg_link -> openTG()
            R.id.gh_link -> openGH()
            R.id.web_link -> openWeb()
            R.id.ll_osl -> startActivity(Intent(context, OSLActivity::class.java))
        }
    }
}