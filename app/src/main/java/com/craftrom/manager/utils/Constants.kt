package com.craftrom.manager.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.craftrom.manager.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform


open class Constants {

    companion object {
        const val TAG = "CraftRomManager"
        const val PREF_SHOW_INTRO = "show_intro"
        const val API_KEY = "AIzaSyAWOzshus6WkrAbbe9Q0XPWpPxV8t2mkyY"

        val NOTIFICATION_CHANNEL_BOOT = "notification_channel_boot"

        const val SPLASH_TIME_OUT: Long = 1000 * 2 // 2 sec
        const val HOST_REFERENCE =
            "https://raw.githubusercontent.com/CraftRom/KernelUpdates/android-10/host"
        const val RSS_FEED_LINK = "https://www.craft-rom.ml/feed.xml"
        const val KERNEL_NAME = "Chidori"
        const val CURRENT_YEAR = "2021"

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
    }
}