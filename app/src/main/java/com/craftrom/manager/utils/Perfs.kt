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

@file:Suppress("DEPRECATION")

package com.craftrom.manager.utils

import android.content.Context
import android.preference.PreferenceManager



/**
 * Created by willi on 01.01.16.
 */
object Prefs {
    fun remove(name: String?, context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(name).apply()
    }

    fun getInt(name: String?, defaults: Int, context: Context?): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(name, defaults)
    }

    fun saveInt(name: String?, value: Int, context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(name, value).apply()
    }

    fun getBoolean(name: String?, defaults: Boolean, context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, defaults)
    }

    fun saveBoolean(name: String?, value: Boolean, context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(name, value).apply()
    }

    fun getString(name: String?, defaults: String?, context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(name, defaults)
    }

    fun saveString(name: String?, value: String?, context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(name, value).apply()
    }

    fun saveStringSet(name: String?, value: Set<String?>?, context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(name, value).apply()
    }

    fun getStringSet(name: String?, defaults: Set<String?>?, context: Context?): Set<String>? {
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(name, defaults)
    }
}