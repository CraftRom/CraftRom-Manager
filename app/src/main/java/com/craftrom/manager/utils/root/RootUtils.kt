/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.craftrom.manager.utils.root

import com.craftrom.manager.utils.Utils.removeSuffix
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import java.util.*

/**
 * Created by willi on 30.12.15.
 */
object RootUtils {
    fun rootAccess(): Boolean {
        return Shell.rootAccess()
    }

    fun chmod(file: String, permission: String) {
        Shell.su("chmod $permission $file").submit()
    }

    fun getProp(prop: String): String {
        return runAndGetOutput("getprop $prop")
    }

    fun mount(command: String, mountPoint: String): String {
        return runAndGetError("mount -o remount,$command $mountPoint")
    }

    val isWritableSystem: Boolean
        get() = mount("rw", "/system") != "mount: '/system' not in /proc/mounts"
    val isWritableRoot: Boolean
        get() = !mount("rw", "/").contains("' is read-only")

    fun closeSU() {
        try {
            Objects.requireNonNull(Shell.getCachedShell())!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun runCommand(command: String?) {
        Shell.su(command).exec()
    }

    fun runAndGetLiveOutput(command: String?, output: List<String?>?) {
        Shell.su(command).to(output, output).exec()
    }

    fun runAndGetOutput(command: String?): String {
        val sb = StringBuilder()
        return try {
            val outputs = Shell.su(command).exec().out
            if (ShellUtils.isValidOutput(outputs)) {
                for (output in outputs) {
                    sb.append(output).append("\n")
                }
            }
            removeSuffix(sb.toString(), "\n")!!.trim { it <= ' ' }
        } catch (e: Exception) {
            ""
        }
    }

    fun runAndGetError(command: String?): String {
        val sb = StringBuilder()
        val outputs: MutableList<String> = ArrayList()
        val stderr: List<String> = ArrayList()
        return try {
            Shell.su(command).to(outputs, stderr).exec()
            outputs.addAll(stderr)
            if (ShellUtils.isValidOutput(outputs)) {
                for (output in outputs) {
                    sb.append(output).append("\n")
                }
            }
            removeSuffix(sb.toString(), "\n")!!.trim { it <= ' ' }
        } catch (e: Exception) {
            ""
        }
    }
}