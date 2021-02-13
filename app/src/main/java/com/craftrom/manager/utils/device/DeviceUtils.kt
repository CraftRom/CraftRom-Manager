package com.craftrom.manager.utils.device

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import java.io.File

object CheckRoot {
    fun isDeviceRooted(): Boolean {
        val su = "su"
        val locations = arrayOf(
                "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
                "/system/sbin/", "/usr/bin/", "/vendor/bin/"
        )
        for (location in locations) {
            if (File(location + su).exists()) {
                return true
            }
        }
        return false
    }
}

object CheckADB {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isAdbEnabled(context: Context): Boolean {
        return Settings.Secure.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED, 0
        ) > 0
    }
}