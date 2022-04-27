package com.craftrom.manager

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.craftrom.manager.activities.IntroActivity
import com.craftrom.manager.databinding.ActivityMainBinding
import com.craftrom.manager.receiver.AlarmReceiver
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.app.AlarmUtil
import com.craftrom.manager.utils.app.AppPrefs
import com.craftrom.manager.utils.ioScope
import com.craftrom.manager.utils.updater.repository.SelfUpdateRepository
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val TAG = "CraftRom:MainActivity"
    private val PERMISSION_REQUEST_CODE = 1234
    private lateinit var mPrefs: SharedPreferences
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val alarmUtil: AlarmUtil by inject()
    private val prefs: AppPrefs by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!mPrefs.getBoolean(Constants.PREF_SHOW_INTRO, false)) {
            mPrefs.edit().putBoolean(Constants.PREF_SHOW_INTRO, true).apply()
            Constants.changeActivity<IntroActivity>(this)
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_news, R.id.nav_kernel, R.id.nav_jitter, R.id.nav_about, R.id.nav_safety, R.id.nav_settings, R.id.nav_others
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Schedule alarm
        alarmUtil.setupAlarm(this@MainActivity)

        // Updates

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            if (prefs.settings.apkUpdate) checkForSelfUpdate() else null

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkForSelfUpdate() = ioScope.launch {
        SelfUpdateRepository().checkForUpdatesAsync(this@MainActivity).await()
            .onFailure { Log.e(this@MainActivity.TAG, "Check for self update error.") }
    }

}