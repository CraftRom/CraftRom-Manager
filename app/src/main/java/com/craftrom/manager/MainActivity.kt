package com.craftrom.manager

import com.craftrom.manager.core.ToolbarTitleProvider
import android.os.Bundle
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.craftrom.manager.databinding.ActivityMainBinding
import com.craftrom.manager.utils.Const.PREF_KEY_DEV_OPTIONS
import com.google.android.material.navigation.NavigationView


class MainActivity : SplashActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun showMainUI(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        val defaultFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)?.childFragmentManager?.fragments?.get(0)
        if (defaultFragment is ToolbarTitleProvider) {
            setToolbarText(defaultFragment.getTitle(), defaultFragment.getSubtitle())
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        // Add a listener for changes to the "devOptions" preference
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            val isVisible = getBoolean(PREF_KEY_DEV_OPTIONS, false)
            navView.menu.findItem(R.id.nav_log).isVisible = isVisible
            navView.menu.findItem(R.id.nav_jitter).isVisible = isVisible
            registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == PREF_KEY_DEV_OPTIONS) {
                    navView.menu.findItem(R.id.nav_log).isVisible =
                        sharedPreferences.getBoolean(PREF_KEY_DEV_OPTIONS, false)
                    navView.menu.findItem(R.id.nav_jitter).isVisible =
                        sharedPreferences.getBoolean(PREF_KEY_DEV_OPTIONS, false)
                }
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        navView.setupWithNavController(navHostFragment.navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setToolbarText(title: String, subtitle: String) {
        supportActionBar?.apply {
            val titleTextView = findViewById<TextView>(R.id.toolbar_title)
            val subtitleTextView = findViewById<TextView>(R.id.toolbar_subtitle)


            titleTextView.text = title
            subtitleTextView.text = subtitle
            titleTextView.maxLines = 1
            subtitleTextView.maxLines = 1

        }
    }
}