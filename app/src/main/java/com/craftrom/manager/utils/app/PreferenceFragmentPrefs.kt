package com.craftrom.manager.utils.app

import android.content.Context
import android.content.SharedPreferences
import com.craftrom.manager.R

class PreferenceFragmentPrefs(private val context: Context, private val prefs: SharedPreferences) {

    var listNews
        get() = prefs.getString(context.getString(R.string.settings_list_news_key), "3")
        set(value) = prefs.edit().putString(context.getString(R.string.settings_list_news_key), value).apply()
    var theme
        get() = prefs.getString(context.getString(R.string.settings_list_themes_key), "0")
        set(value) = prefs.edit().putString(context.getString(R.string.settings_list_themes_key), value).apply()
    var fontSize
        get() = prefs.getInt(context.getString(R.string.settings_web_font_size_key), 14)
        set(value) = prefs.edit().putInt(context.getString(R.string.settings_web_font_size_key), value).apply()
}