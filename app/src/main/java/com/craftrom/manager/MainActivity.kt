package com.craftrom.manager

import android.content.SharedPreferences
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.craftrom.manager.core.ServiceContext
import com.craftrom.manager.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : SplashActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun showMainUI(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        // Add a listener for changes to the "devOptions" preference
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val devOptions = sharedPreferences.getBoolean("devOptions", false)
        val navJitterItem = navView.menu.findItem(R.id.nav_jitter)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "devOptions") {
                val isVisible = sharedPreferences.getBoolean("devOptions", false)
                navJitterItem.isVisible = isVisible
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        navJitterItem.isVisible = devOptions

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}