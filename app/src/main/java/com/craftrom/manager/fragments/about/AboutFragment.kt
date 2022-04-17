package com.craftrom.manager.fragments.about

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


//    private lateinit var osl: LinearLayout
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
//        osl = root.findViewById(R.id.ll_osl)
//        osl.setOnClickListener(this)
        imageView = root.findViewById(R.id.tg_link)
        imageView.setOnClickListener(this)
        ghimg = root.findViewById(R.id.gh_link)
        ghimg.setOnClickListener(this)
        webimg = root.findViewById(R.id.web_link)
        webimg.setOnClickListener(this)

        versionApp = root.findViewById(R.id.version)

        val versionName = BuildConfig.VERSION_NAME
        versionApp.text = versionName

        root.findViewById<Button>(R.id.legal).setOnClickListener {
            // on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(requireContext(), R.style.ThemeBottomSheet)

            // on below line we are inflating a layout file which we have created.
            val card = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_contributor, null)

            val btnClose = card.findViewById<Button>(R.id.negativeButton)

            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialog.setCancelable(true)

            // on below line we are setting
            // content view to our view.
            dialog.setContentView(card)
            dialog.dismissWithAnimation = true
            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()

        }
        return root
    }
    private fun openTG() {
        val uri = Uri.parse("https://t.me/craft_rom")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        //intent.setPackage("org.thunderdog.challegram");
        startActivity(intent)
    }

    private fun openGH() {
        val uri = Uri.parse("https://github.com/CraftRom")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
    private fun openWeb() {
        val uri = Uri.parse("https://www.craft-rom.ml/")
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