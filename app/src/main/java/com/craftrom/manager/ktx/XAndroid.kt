package com.craftrom.manager.ktx

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.craftrom.manager.R
import java.util.*

fun ViewGroup.startAnimations() {
    val transition = AutoTransition()
        .setInterpolator(FastOutSlowInInterpolator())
        .setDuration(400)
        .excludeTarget(R.id.toolbar, true)
    TransitionManager.beginDelayedTransition(
        this,
        transition
    )
}
val Context.deviceProtectedContext: Context
    get() =
        this
