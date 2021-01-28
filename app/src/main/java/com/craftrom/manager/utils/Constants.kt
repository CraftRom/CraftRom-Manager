package com.craftrom.manager.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast

class Constants {

    companion object {
        const val SPLASH_TIME_OUT: Long = 1000 * 1 // 3 sec
        private const val SHARED_PREF_NAME = "CravXSharedPreferences"
        private lateinit var sharedPreferences: SharedPreferences
        const val IS_FIRST_TIME: String = "IS_FIRST_TIME"
        const val IS_LOG_OUT: String = "IS_LOG_OUT"

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
    }
}