package com.craftrom.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.craftrom.manager.services.FPS
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class MainApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var fps: FPS? = null
        private var bootSharedPreferences: SharedPreferences? = null
        var iNSTANCE: MainApplication? = null
            private set
        private var firstBoot = false

        fun setFPS(fps: FPS?) {
            this.fps = fps
        }
        fun isFirstBoot(): Boolean {
            Log.w (Constants.TAG, "First BOOT: $firstBoot")
            return firstBoot
        }

        val sharedPreferences: SharedPreferences
            get() = iNSTANCE!!.getSharedPreferences("mmm", MODE_PRIVATE)

        fun getBootSharedPreferences(): SharedPreferences? {
            return bootSharedPreferences
        }
    }

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences: SharedPreferences = sharedPreferences
        // We are only one process so it's ok to do this
            if (!firstBoot) {
                sharedPreferences.edit().putBoolean("first_boot", false).apply()
            }
        initKoin()

    }

    init {
        check(iNSTANCE == null) { "Duplicate application instance!" }
        iNSTANCE = this
    }
    private fun initKoin() = startKoin{
        androidContext(this@MainApplication)
        modules(mainModule)
    }
}