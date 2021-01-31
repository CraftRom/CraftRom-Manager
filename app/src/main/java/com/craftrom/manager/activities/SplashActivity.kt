package com.craftrom.manager.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.FileUtils
import com.craftrom.manager.utils.root.RootUtils.rootAccess
import kotlinx.android.synthetic.main.activity_main.*


class SplashActivity : AppCompatActivity() {
    private var isFirstTime: Boolean = true
    private var isLogout: Boolean? = null
    private var coordLayout: CoordinatorLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coordinator_splash)

        coordLayout = findViewById(R.id.content)

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
                    Constants.showSnackbar(coordLayout, getString(R.string.error_root,)
                    )
                    Constants.changeActivity<MainActivity>(this@SplashActivity)
                    finish()
                }
            }


        }, Constants.SPLASH_TIME_OUT)
    }
}

