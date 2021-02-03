package com.craftrom.manager.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.activities.into.IntoWelcomeActivity
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.SharedPreferenceUtils
import kotlinx.android.synthetic.main.activity_main.*


class SplashActivity : AppCompatActivity() {
    private var coordLayout: CoordinatorLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coordinator_splash)
        coordLayout = findViewById(R.id.content)

        if(!SharedPreferenceUtils.getBooleanPreferenceValue(this,"isFirstTimeExecution")){
            SharedPreferenceUtils.setBooleanPreferenceValue(this,"isFirstTimeExecution",true)
             // do your first time execution stuff here,
            Handler(Looper.getMainLooper()).postDelayed({

                Constants.changeActivity<IntoWelcomeActivity>(this@SplashActivity)
                finish()

            }, Constants.SPLASH_TIME_OUT)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({

                Constants.changeActivity<MainActivity>(this@SplashActivity)
                finish()

            }, Constants.SPLASH_TIME_OUT)
        }






    }


}

