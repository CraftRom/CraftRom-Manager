package com.craftrom.manager.utils.app

import android.content.Context
import android.content.SharedPreferences
import com.craftrom.manager.R

class PreferenceFragmentPrefs(private val context: Context, private val prefs: SharedPreferences) {

    var listNews
        get() = prefs.getString(context.getString(R.string.settings_list_news_key), "3")
        set(value) = prefs.edit().putString(context.getString(R.string.settings_list_news_key), value).apply()
    var apkUpdate
        get() = prefs.getBoolean(context.getString(R.string.settings_apkupdate_key), true)
        set(value) = prefs.edit().putBoolean(context.getString(R.string.settings_apkupdate_key), value).apply()
    var checkForUpdates
        get() = prefs.getString(context.getString(R.string.settings_check_for_updates_key), "0")
        set(value) = prefs.edit().putString(context.getString(R.string.settings_check_for_updates_key), value).apply()

    var updateHour
        get() = prefs.getInt(context.getString(R.string.settings_update_hour_key), 12)
        set(value) = prefs.edit().putInt(context.getString(R.string.settings_update_hour_key), value).apply()
    var rootProfile
        get() = prefs.getBoolean(context.getString(R.string.settings_root_profile_key), false)
        set(value) = prefs.edit().putBoolean(context.getString(R.string.settings_root_profile_key), value).apply()
}