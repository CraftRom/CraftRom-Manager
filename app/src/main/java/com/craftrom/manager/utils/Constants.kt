package com.craftrom.manager.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import com.craftrom.manager.R
import com.google.android.material.snackbar.Snackbar

class Constants {

    companion object {
        const val SPLASH_TIME_OUT: Long = 1000 * 1 // 3 sec
        private const val SHARED_PREF_NAME = "CravXSharedPreferences"
        private lateinit var sharedPreferences: SharedPreferences
        const val IS_FIRST_TIME: String = "IS_FIRST_TIME"
        const val IS_LOG_OUT: String = "IS_LOG_OUT"

        inline fun <reified T : Activity> changeActivity(activity: Activity) {
            val intent = Intent(activity, T::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        fun showToastMessage(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        }

        fun getIsFirstTime(context: Context?): Boolean {
            sharedPreferences = getSharedPreferences(context!!)
            return try {
                sharedPreferences.getString(IS_FIRST_TIME, "")?.toBoolean()?:true
            } catch (e: Exception) {
                true
            }
        }

        fun getIsLogout(context: Context?): Boolean? {
            sharedPreferences = getSharedPreferences(context!!)
            return try {
                sharedPreferences.getString(IS_LOG_OUT, "")?.toBoolean()
            } catch (e: Exception) {
                true
            }
        }

        fun showSnackbar(view: View?, message: String?) {
            val snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
                .setAction(R.string.dismiss, object : View.OnClickListener {
                    override fun onClick(view: View?) {}
                })
            snackbar.show()
        }

    }
}