package com.craftrom.manager.core

import android.annotation.SuppressLint
import android.content.Context
import com.craftrom.manager.ktx.deviceProtectedContext

val AppContext: Context inline get() = ServiceContext.context

@SuppressLint("StaticFieldLeak")
object ServiceContext {

    lateinit var context: Context
    val deContext by lazy { context.deviceProtectedContext }
}