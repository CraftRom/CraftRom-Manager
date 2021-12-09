package com.craftrom.manager.activities.splash

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.Constants.Companion.SPLASH_TIME_OUT
import java.util.*


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var imageLogo: ImageView
    private  lateinit var textViewVersion: TextView
    private  lateinit var textViewSlogan: TextView
    private  lateinit var textCopirate: TextView

    private val alphaAnimation by lazy {
        AlphaAnimation(0.0f, 1.0f).apply {
            duration = SPLASH_TIME_OUT
            fillAfter = true
        }
    }

    private val scaleAnimation by lazy {
        ScaleAnimation(
            0.65f,
            1f,
            0.65f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = SPLASH_TIME_OUT
            fillAfter = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        imageLogo = findViewById(R.id.imageLogo)
        textViewVersion = findViewById(R.id.textViewVersion)
        textViewSlogan = findViewById(R.id.textViewSlogan)
        textCopirate = findViewById(R.id.text)
        initView()

    }

    private fun initView() {
        val appName = resources.getString(R.string.app_name)
        textCopirate.text = resources.getString(R.string.copyr)
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            textViewVersion.text = resources.getString(R.string.app_name_version, appName, packageInfo.versionName)
        } catch (e: PackageManager.NameNotFoundException) {
        }


        Glide.with(this)
            .load(R.drawable.ic_launcher_2)
            .into(imageLogo)

        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                Constants.changeActivity<MainActivity>(this@SplashActivity)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // TODO("Not yet implemented")
            }
        })

        imageLogo.startAnimation(scaleAnimation)
        textViewSlogan.startAnimation(alphaAnimation)
    }


}

