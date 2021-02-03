package com.craftrom.manager.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SharedPreferenceUtils {
    /**
     * Sets the boolean preference value
     *
     * @param context the current context
     * @param key     the preference key
     * @param value   the value to be set
     */
    fun setBooleanPreferenceValue(context: Context?, key: String?, value: Boolean) {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putBoolean(key, value).apply()
    }

    /**
     * Get the boolean preference value from the SharedPreference
     *
     * @param context the current context
     * @param key     the preference key
     * @return the the preference value
     */
    fun getBooleanPreferenceValue(context: Context?, key: String?): Boolean {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(key, false)
    }
}