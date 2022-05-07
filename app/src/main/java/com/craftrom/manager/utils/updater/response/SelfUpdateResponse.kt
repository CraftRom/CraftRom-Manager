package com.craftrom.manager.utils.updater.response

data class SelfUpdateResponse(
    val version: Int = 0,
    val apkversion: Float = 0.0F,
    val apk: String = "",
    val changelog: String = ""
)