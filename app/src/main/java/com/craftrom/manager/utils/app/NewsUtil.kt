package com.craftrom.manager.utils.app

import com.craftrom.manager.ui.home.HomeFragment
import org.koin.core.component.KoinComponent

class NewsUtil(private val prefs: AppPrefs) : KoinComponent {

    fun setupListCount() {
        val interval = getInterval()
        HomeFragment.LIST_LIMIT = interval
    }

    private fun getInterval(): Int {
        return when (prefs.settings.listNews) {
            "0" -> 3
            "1" -> 5
            "2" -> 10
            else -> 15
        }
    }
}
