package com.craftrom.manager.fragments.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.craftrom.manager.R
import com.craftrom.manager.utils.app.AlarmUtil
import com.craftrom.manager.utils.app.NewsUtil
import org.koin.android.ext.android.inject


class SettingsFragment : PreferenceFragmentCompat() {

    private val newsUtil: NewsUtil by inject()
    private val alarmUtil: AlarmUtil by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<ListPreference>(getString(R.string.settings_list_news_key))?.setOnPreferenceChangeListener { _, _ ->
            context?.let { newsUtil.setupListCount() }
            true
        }

        findPreference<ListPreference>(getString(R.string.settings_check_for_updates_key))?.setOnPreferenceChangeListener { _, _ ->
            context?.let { alarmUtil.setupAlarm(it) }
            true
        }

        findPreference<SeekBarPreference>(getString(R.string.settings_update_hour_key))?.setOnPreferenceChangeListener { _, _ ->
            context?.let { alarmUtil.setupAlarm(it) }
            true
        }

    }
}