package com.craftrom.manager.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.FileUtils
import com.craftrom.manager.utils.root.CheckRoot

class SplashActivity : AppCompatActivity() {
    private val splashTime = 3000L // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            FileUtils.setFilePermissions().submit {
                if (CheckRoot.isDeviceRooted) {
                    gotoMainActivity()
                } else {
                    gotoNoRootActivity()
                }

            }
        }, splashTime)

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
