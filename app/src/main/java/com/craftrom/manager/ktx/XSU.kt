package com.craftrom.manager.ktx

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@JvmField var recovery = false

fun reboot(reason: String = if (recovery) "recovery" else "") {
    if (reason == "recovery") {
        // KEYCODE_POWER = 26, hide incorrect "Factory data reset" message
        Shell.cmd("/system/bin/input keyevent 26").submit()
    }
    Shell.cmd("/system/bin/svc power reboot $reason || /system/bin/reboot $reason").submit()
}

suspend fun Shell.Job.await() = withContext(Dispatchers.IO) { exec() }
