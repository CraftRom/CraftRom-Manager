package com.craftrom.manager.utils.app

import android.content.Context
import android.content.SharedPreferences
import com.craftrom.manager.R

class PreferenceFragmentPrefs(private val context: Context, private val prefs: SharedPreferences) {

    var listNews
        get() = prefs.getString(context.getString(R.string.settings_list_news_key), "1")
        set(value) = prefs.edit().putString(context.getString(R.string.settings_list_news_key), value).apply()
    var theme
        get() = prefs.getString(context.getString(R.string.settings_list_themes_key), "0")
        set(value) = prefs.edit().putString(context.getString(R.string.settings_list_themes_key), value).apply()
    var darkTheme
        get() = prefs.getString(context.getString(R.string.settings_dark_theme_key), "0")
        set(value) = prefs.edit().putString(context.getString(R.string.settings_dark_theme_key), value).apply()
}