package com.craftrom.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.preference.PreferenceManager
import com.craftrom.manager.core.ServiceContext
import com.craftrom.manager.utils.Const.PREF_KEY_ROOT_ENABLE
import com.craftrom.manager.utils.app.AppPrefs
import com.craftrom.manager.utils.theme.ThemeType
import com.craftrom.manager.utils.theme.applyTheme
import com.topjohnwu.superuser.Shell
import org.koin.android.ext.android.inject
import java.util.concurrent.Executors

@SuppressLint("CustomSplashScreen")
abstract class SplashActivity : AppCompatActivity() {

    private val prefs: AppPrefs by inject()
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(
        ServiceContext.context) }

    companion object {
        private var skipSplash = false
    }

    private val getPermissions = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme()
        setDark()

        installSplashScreen().apply {
            setKeepOnScreenCondition { !skipSplash }
        }

        if (skipSplash) {
            showMainUI(savedInstanceState)
        } else {
            if (!hasInstallPermissions()) {
                checkUnknownResourceInstallation()
            } else {
                preLoad(savedInstanceState)
            }
        }
    }

    private fun setTheme() {
        when (prefs.settings.theme) {
            "0" -> setTheme(R.style.ThemeFoundationColor_Default)
            "1" -> setTheme(R.style.ThemeFoundationColor_Amoled)
            "2" -> setTheme(R.style.ThemeFoundationColor_Aubergine)
            "3" -> setTheme(R.style.ThemeFoundationColor_Green)
            else -> setTheme(R.style.ThemeFoundationColor_Red)
        }
    }

    private fun setDark() {
        when (prefs.settings.darkTheme) {
            "0" -> applyTheme(ThemeType.DEFAULT_MODE)
            "1" -> applyTheme(ThemeType.LIGHT_MODE)
            else -> applyTheme(ThemeType.DARK_MODE)
        }
    }

    private fun preLoad(savedState: Bundle?) {
        Shell.getShell(Shell.EXECUTOR) {
            if (!it.isRoot) {
                runOnUiThread {
                    sharedPreferences.edit().putBoolean(PREF_KEY_ROOT_ENABLE, false).apply()
                    skipSplash = true
                    showMainUI(savedState)
                }
                return@getShell
            }

            runOnUiThread {
                sharedPreferences.edit().putBoolean(PREF_KEY_ROOT_ENABLE, true).apply()
                skipSplash = true
                showMainUI(savedState)
            }
        }
    }

    private fun hasInstallPermissions(): Boolean {
        return packageManager.canRequestPackageInstalls()
    }

    private fun checkUnknownResourceInstallation() {
        getPermissions.launch(
            Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            )
        )
    }

    abstract fun showMainUI(savedInstanceState: Bundle?)
}