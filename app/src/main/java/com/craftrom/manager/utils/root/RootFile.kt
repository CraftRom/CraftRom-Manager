/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftrom.manager.utils.root

import com.craftrom.manager.utils.root.RootUtils
import com.craftrom.manager.utils.root.RootFile

/*
 * Originally created by willi on 30.12.15.
 * Modified by sunilpaulmathew <sunil.kde@gmail.com> on April 18, 2021
 */
class RootFile {
    fun isEmpty(mFile: String): Boolean {
        return "false" == RootUtils.runAndGetOutput("find '$mFile' -mindepth 1 | read || echo false")
    }

    companion object {
        fun delete(mFile: String) {
            RootUtils.runCommand("rm -r '$mFile'")
        }

        fun execute(mFile: String) {
            RootUtils.runCommand("sh $mFile")
        }

        fun exists(mFile: String): Boolean {
            val output = RootUtils.runAndGetOutput("[ -e $mFile ] && echo true")
            return !output.isEmpty() && output == "true"
        }

        fun mv(mFile: String, newPath: String) {
            RootUtils.runCommand("mv -f '$mFile' '$newPath'")
        }

        fun read(mFile: String): String {
            return RootUtils.runAndGetOutput("cat '$mFile'")
        }

        fun write(mFile: String, text: String, append: Boolean) {
            val array = text.split("\\r?\\n").toTypedArray()
            if (!append) delete(mFile)
            for (line in array) {
                RootUtils.runCommand("echo '$line' >> $mFile")
            }
            RootUtils.chmod(mFile, "755")
        }
    }
}