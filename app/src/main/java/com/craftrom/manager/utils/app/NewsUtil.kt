package com.craftrom.manager.utils.app

import com.craftrom.manager.fragments.news.NewsFragment
import org.koin.core.component.KoinComponent

class NewsUtil(private val prefs: AppPrefs): KoinComponent {

    fun setupListCount() = listCount()

    private fun listCount (interval: Int = getInterval()) {

        when(prefs.settings.listNews) {
            "0" -> {
                NewsFragment.LIST_LIMIT = interval
            }
            "1" -> {
                NewsFragment.LIST_LIMIT = interval
                }
            "2" -> {
                NewsFragment.LIST_LIMIT = interval
            }
            }
        }


    private fun getInterval() = when(prefs.settings.listNews) {
        "0" -> 3
        "1" -> 5
        else -> 10
    }
}