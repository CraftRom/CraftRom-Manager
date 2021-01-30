package com.craftrom.manager.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.FileUtils
import com.craftrom.manager.utils.root.RootUtils.rootAccess

class SplashActivity : AppCompatActivity() {
    private var isFirstTime: Boolean = true
    private var isLogout: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        isFirstTime = Constants.getIsFirstTime(this@SplashActivity)
        isLogout = Constants.getIsLogout(this@SplashActivity)

        Handler(Looper.getMainLooper()).postDelayed({
            /*
             * If root are available
             */
            if (rootAccess()) {
                Constants.changeActivity<MainActivity>(this@SplashActivity)
                finish()
            } else {
                /*
                    * If root or busybox/toybox are not available,
                    * * launch text activity which let the user know
                    * * what the problem is.
                    * */
                if (!FileUtils.mHasRoot || !FileUtils.mHasBusybox) {
                    Constants.showToastMessage(this, resources.getString(R.string.error_root))
                    Constants.changeActivity<MainActivity>(this@SplashActivity)
                    finish()
                }
            }


        }, Constants.SPLASH_TIME_OUT)
    }
}

