package com.craftrom.manager.fragments.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants.Companion.STATE_CURRENT_LIST_LIMIT


class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var mPrefs: SharedPreferences
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        mPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val lp = findPreference("newsarticle") as ListPreference?
        lp?.summary = STATE_CURRENT_LIST_LIMIT

        lp?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue.toString()
                val currentValue = lp!!.value.toInt()
                val editor: SharedPreferences.Editor = mPrefs.edit()
                editor.putInt(STATE_CURRENT_LIST_LIMIT, currentValue)
                editor.apply()
                true
            }

    }
}