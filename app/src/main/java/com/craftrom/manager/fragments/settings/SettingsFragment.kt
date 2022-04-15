package com.craftrom.manager.fragments.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.craftrom.manager.R
import com.craftrom.manager.utils.app.NewsUtil
import org.koin.android.ext.android.inject


class SettingsFragment : PreferenceFragmentCompat() {

    private val newsUtil: NewsUtil by inject()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<ListPreference>(getString(R.string.settings_list_news_key))?.setOnPreferenceChangeListener { _, _ ->
            context?.let { newsUtil.setupListCount(it) }
            true
        }

    }
}