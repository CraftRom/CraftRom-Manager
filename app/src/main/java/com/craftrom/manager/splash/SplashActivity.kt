package com.craftrom.manager.splash

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.craftrom.manager.BuildConfig
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {
    private var coordLayout: CoordinatorLayout? = null
    private val splashDuration = 5 * 1000L

    private val alphaAnimation by lazy {
        AlphaAnimation(0.0f, 1.0f).apply {
            duration = splashDuration
            fillAfter = true
        }
    }

    private val scaleAnimation by lazy {
        ScaleAnimation(
                1f,
                1.25f,
                1f,
                1.25f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        ).apply {
            duration = splashDuration
            fillAfter = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initView()

    }

    private fun initView() {
        val appName = resources.getString(R.string.app_name)
        textViewVersion.text =
                resources.getString(R.string.app_name_version, appName, BuildConfig.VERSION_NAME)

        Glide.with(this)
                .load(R.drawable.splash)
                .into(imageBackground)

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

        imageBackground.startAnimation(scaleAnimation)
        textViewSlogan.startAnimation(alphaAnimation)
    }


}

