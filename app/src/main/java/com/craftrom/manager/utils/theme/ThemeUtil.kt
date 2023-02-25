package com.craftrom.manager.utils.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode

fun applyTheme(theme: ThemeType) {
    when(theme) {
        ThemeType.LIGHT_MODE -> {
            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        ThemeType.DARK_MODE -> {
            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        ThemeType.DEFAULT_MODE -> {
            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}