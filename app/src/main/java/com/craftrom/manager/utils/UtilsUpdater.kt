package com.craftrom.manager.utils

import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.preference.PreferenceManager
import com.craftrom.manager.R
import java.io.File


@Suppress("DEPRECATION")
object UtilsUpdater {
    private val TAG = "Utils"

    fun getDownloadPath(context: Context): File {
        return File(context.getString(R.string.download_path_chidori))
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return !(info == null || !info.isConnected || !info.isAvailable)
    }
    fun getUpdateCheckSetting(context: Context?): Int {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
            Constants.PREF_AUTO_UPDATES_CHECK_INTERVAL,
            Constants.AUTO_UPDATES_CHECK_INTERVAL_DAILY
        )
    }

    fun getUpdateCheckInterval(context: Context?): Long {
        return when (getUpdateCheckSetting(context)) {
            Constants.AUTO_UPDATES_CHECK_INTERVAL_DAILY -> AlarmManager.INTERVAL_DAY
            Constants.AUTO_UPDATES_CHECK_INTERVAL_WEEKLY -> AlarmManager.INTERVAL_DAY * 7
            Constants.AUTO_UPDATES_CHECK_INTERVAL_MONTHLY -> AlarmManager.INTERVAL_DAY * 30
            else -> AlarmManager.INTERVAL_DAY * 7
        }
    }
}
