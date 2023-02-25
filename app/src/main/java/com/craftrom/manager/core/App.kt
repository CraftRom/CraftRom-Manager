package com.craftrom.manager.core

import android.app.Application
import android.content.Context
import com.craftrom.manager.utils.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ServiceContext.context = base

        // Pre-heat the shell ASAP
        // Shell.getShell(null) {}
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(mainModule)
        }
    }
}
