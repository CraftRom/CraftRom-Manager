package com.craftrom.manager.utils.updater.response

data class KernelUpdateResponse(
    val chidori: Int = 0,
    val kernel: String = "",
    val date: String = "",
    val changelog: String = "",
    val commit: String = "",
    val downloadLink: String = ""
    )