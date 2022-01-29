package com.craftrom.manager

import android.app.Application
import com.craftrom.manager.services.FPS
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class App : Application() {
    private var fps: FPS? = null
    private var dismissDialog: BottomSheetDialog? = null
    private val bootSharedPreferences: SharedPreferences? = null
    private var INSTANCE: App? = null

    fun App() {
        check(INSTANCE == null) { "Duplicate application instance!" }
        INSTANCE = this
    }

    val isFpsRunning: Boolean
        get() = fps != null

    fun setFPS(fps: FPS?) {
        this.fps = fps
    }

    fun setDialogToDismiss(dialog: BottomSheetDialog?) {
        dismissDialog = dialog
    }

    fun dismissDialog() {
        if (dismissDialog != null) dismissDialog!!.dismiss()
    }

    fun getBootSharedPreferences(): SharedPreferences? {
        return bootSharedPreferences
    }
    override fun onCreate() {
        super.onCreate()
    }
}