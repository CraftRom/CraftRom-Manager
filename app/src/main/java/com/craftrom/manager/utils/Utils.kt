package com.craftrom.manager.utils

import android.content.Context
import android.widget.Toast
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils

object Utils {
    fun rootCheck(ctx: Context?): Boolean {
        if (Shell.rootAccess()) return true
        Toast.makeText(ctx, "No Root !", Toast.LENGTH_SHORT).show()
        return false
    }

    fun getProp(prop: String): String {
        return ShellUtils.fastCmd("getprop $prop")
    }

    fun setProp(prop: String, value: String) {
        ShellUtils.fastCmd("setprop $prop $value")
    }

    fun needRebootToast(ctx: Context?) {
        Toast.makeText(ctx, "Take effect on next reboot", Toast.LENGTH_SHORT).show()
    }
}
