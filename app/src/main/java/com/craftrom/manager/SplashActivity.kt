package com.craftrom.manager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.craftrom.manager.utils.app.AppPrefs
import com.topjohnwu.superuser.Shell
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
abstract class SplashActivity : AppCompatActivity() {
    private val prefs: AppPrefs by inject()
    companion object {
        private var skipSplash = false
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                !skipSplash
            }
        }

        if (skipSplash) {
            showMainUI(savedInstanceState)
        } else {
            Shell.getShell(Shell.EXECUTOR) {
//                if (!it.isRoot) {
//                    return@getShell
//                }
                preLoad(savedInstanceState)
            }
        }
    }

    abstract fun showMainUI(savedInstanceState: Bundle?)

    private fun setTheme() {
        when (prefs.settings.theme) {
            "0" -> setTheme(R.style.ThemeFoundationColor_Default)
            "1" -> setTheme(R.style.ThemeFoundationColor_Amoled)
            "2" -> setTheme(R.style.ThemeFoundationColor_Aubergine)
            "3" -> setTheme(R.style.ThemeFoundationColor_Green)
            else -> setTheme(R.style.ThemeFoundationColor_Red)
        }
    }

    private fun preLoad(savedState: Bundle?) {
        runOnUiThread {
            skipSplash = true
                showMainUI(savedState)

        }
    }
}

