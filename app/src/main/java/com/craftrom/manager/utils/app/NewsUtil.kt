package com.craftrom.manager.utils.app

import com.craftrom.manager.ui.home.HomeFragment
import org.koin.core.component.KoinComponent

class NewsUtil(private val prefs: AppPrefs): KoinComponent {

    fun setupListCount() = listCount()

    private fun listCount (interval: Int = getInterval()) {

        when(prefs.settings.listNews) {
            "0" -> {
                HomeFragment.LIST_LIMIT = interval
            }
            "1" -> {
                HomeFragment.LIST_LIMIT = interval
                }
            "2" -> {
                HomeFragment.LIST_LIMIT = interval
            }
            "3" -> {
                HomeFragment.LIST_LIMIT = interval
            }
            }
        }


    private fun getInterval() = when(prefs.settings.listNews) {
        "0" -> 3
        "1" -> 5
        "2" -> 10
        else -> 15
    }
}