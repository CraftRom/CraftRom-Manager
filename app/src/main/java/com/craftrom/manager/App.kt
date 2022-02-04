package com.craftrom.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.craftrom.manager.services.FPS

class App : Application() {
    private var fps: FPS? = null
    private var INSTANCE: App? = null

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    fun App() {
        check(INSTANCE == null) { "Duplicate application instance!" }
        INSTANCE = this
    }

    fun setFPS(fps: FPS?) {
        this.fps = fps
    }

    companion object {
        /**
         * 获取Context上下文
         *
         * @return the context
         */
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }

}