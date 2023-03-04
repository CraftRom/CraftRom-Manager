package com.craftrom.manager.ui.settings

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.app.NewsUtil
import org.koin.android.ext.android.inject


class SettingsFragment : PreferenceFragmentCompat() {

    private val newsUtil: NewsUtil by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).supportActionBar
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        val mainActivity = requireActivity() as MainActivity
        val title: String = getString(R.string.settings)
        val subtitle: String = getString(R.string.subtitle_settings)
        mainActivity.setToolbarText(title, subtitle)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)


        findPreference<ListPreference>(getString(R.string.settings_list_themes_key))?.setOnPreferenceChangeListener { _, _ ->
            activity?.recreate()
            true
        }
        findPreference<ListPreference>(getString(R.string.settings_dark_theme_key))?.setOnPreferenceChangeListener { _, _ ->
            activity?.recreate()
            true
        }

        findPreference<ListPreference>(getString(R.string.settings_list_news_key))?.setOnPreferenceChangeListener { _, _ ->
            context?.let { newsUtil.setupListCount() }
            true
        }

    }
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}