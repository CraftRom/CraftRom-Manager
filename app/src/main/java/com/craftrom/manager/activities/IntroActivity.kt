package com.craftrom.manager.activities;

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        findViewById<Button>(R.id.startApp).setOnClickListener {
            Constants.changeActivity<MainActivity>(this) }

        findViewById<Button>(R.id.moreInfo).setOnClickListener {
            // on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(this, R.style.ThemeBottomSheet)

            // on below line we are inflating a layout file which we have created.
            val card = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_contributor, null)

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
    }
}
