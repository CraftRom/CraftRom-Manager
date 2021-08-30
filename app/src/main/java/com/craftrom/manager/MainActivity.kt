package com.craftrom.manager

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.craftrom.manager.activities.IntroActivity
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.Device
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private val TAG = "CraftRom:MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "device = " + Device.getDeviceName())
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (!mPrefs.getBoolean(Constants.PREF_SHOW_INTRO, false)) {
            mPrefs.edit().putBoolean(Constants.PREF_SHOW_INTRO, true).apply()
            Constants.changeActivity<IntroActivity>(this)
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_news,
                R.id.nav_device,
                R.id.nav_kernel,
                R.id.nav_rom,
                R.id.nav_module,
                R.id.nav_terminal,
                R.id.nav_setting,
                R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}