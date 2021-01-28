package com.craftrom.manager.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.FileUtils
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
                  if (isFirstTime || isLogout == true) {
                      Constants.showToastMessage(this, "Your Device Is Rooted")
                    gotoMainActivity() } else {
                      Constants.showToastMessage(this, "Your Device Is Rooted")
                      gotoMainActivity()
                  }
                } else {
                    Toast.makeText(this, "Device NOT Rooted! ", Toast.LENGTH_SHORT).show()
                    gotoNoRootActivity()
                }

        }, Constants.SPLASH_TIME_OUT)

    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (getIntent().extras != null) {
            intent.putExtras(getIntent().extras!!)
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun gotoNoRootActivity() {
        val intent = Intent(this, NoRootActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (getIntent().extras != null) {
            intent.putExtras(getIntent().extras!!)
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
