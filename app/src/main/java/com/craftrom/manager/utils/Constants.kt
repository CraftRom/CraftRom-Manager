package com.craftrom.manager.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.craftrom.manager.R


open class Constants {

    companion object {
        const val SPLASH_TIME_OUT: Long = 1000 * 1 // 3 sec

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
    }
}