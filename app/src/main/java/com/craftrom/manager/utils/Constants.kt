package com.craftrom.manager.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.craftrom.manager.R
import com.google.android.material.snackbar.Snackbar


open class Constants {

    companion object {
        const val TAG = "CraftRomManager"
        const val PREF_SHOW_INTRO = "show_intro"
        const val API_KEY = "AIzaSyAWOzshus6WkrAbbe9Q0XPWpPxV8t2mkyY"

        const val SPLASH_TIME_OUT = 2000L // 2 sec
        const val UPDATE_TIME_OUT =  200L // 0.2 sec

        const val RSS_FEED_LINK = "https://www.craft-rom.pp.ua/feed.xml"
        var STATE_CURRENT_LIST_LIMIT = 5
        const val CURRENT_YEAR = "2022"

        inline fun <reified T : Activity> changeActivity(activity: Activity) {
            val intent = Intent(activity, T::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            activity.finish()
        }

        fun showToastMessage(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun showSnackMessage(view: View, msg : String){
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
        }

        fun share (context: Context, title: String, url: String){
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
                // (Optional) Here we're setting the title of the content
                putExtra(Intent.EXTRA_TITLE, title)

                // (Optional) Here we're passing a content URI to an image to be displayed
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }, null)
            context.startActivity(share)
        }
    }
}