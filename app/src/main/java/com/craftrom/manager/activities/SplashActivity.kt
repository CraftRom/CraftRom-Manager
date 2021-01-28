package com.craftrom.manager.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.root.CheckRoot

class SplashActivity : AppCompatActivity() {
    private var isFirstTime: Boolean = true
    private var isLogout: Boolean ?= null
    private val rootCheck = CheckRoot.isDeviceRooted

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        isFirstTime = Constants.getIsFirstTime(this@SplashActivity)
        isLogout = Constants.getIsLogout(this@SplashActivity)

        Handler(Looper.getMainLooper()).postDelayed({
            // Tamper Checking and restrictions for installing the application on rooted devices
              if (rootCheck) {
                      Constants.showToastMessage(this, "Your Device Is Rooted")
                      Constants.changeActivity<MainActivity>(this@SplashActivity)
                      finish()
                } else {
                    Constants.showToastMessage(this, "Your Device Is NOT Rooted")
                    Constants.changeActivity<NoRootActivity>(this@SplashActivity)
                  finish()
                }

        }, Constants.SPLASH_TIME_OUT)

    }
}
