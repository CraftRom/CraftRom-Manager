package com.craftrom.manager.core

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.craftrom.manager.core.ServiceContext
import com.craftrom.manager.utils.mainModule
import com.topjohnwu.superuser.Shell
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class App : Application() {
    private var iNSTANCE: App? = null
    override fun onCreate() {
        super.onCreate()
        initKoin()

    }
    override fun attachBaseContext(context: Context) {
        val base: Context = context
        super.attachBaseContext(base)
        ServiceContext.context = base

        // Pre-heat the shell ASAP
        Shell.getShell(null) {}
    }
    init {
        check(iNSTANCE == null) { "Duplicate application instance!" }
        iNSTANCE = this
    }
    private fun initKoin() = GlobalContext.startKoin {
        androidContext(this@App)
        modules(mainModule)
    }
}
