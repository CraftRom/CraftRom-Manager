package com.craftrom.manager.utils.app

import android.content.Context
import android.content.SharedPreferences
import com.craftrom.manager.R

class PreferenceFragmentPrefs(private val context: Context, private val prefs: SharedPreferences) {

//    var checkForUpdates
//        get() = prefs.getString(context.getString(R.string.settings_check_for_updates_key), "0")
//        set(value) = prefs.edit().putString(context.getString(R.string.settings_check_for_updates_key), value).apply()

    var listNews
        get() = prefs.getString(context.getString(R.string.settings_list_news_key), "3")
        set(value) = prefs.edit().putString(context.getString(R.string.settings_list_news_key), value).apply()

}