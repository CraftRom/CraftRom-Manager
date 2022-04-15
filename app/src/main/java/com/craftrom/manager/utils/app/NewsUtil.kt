package com.craftrom.manager.utils.app

import android.content.Context
import com.craftrom.manager.fragments.news.NewsFragment
import org.koin.core.component.KoinComponent

class NewsUtil(private val context: Context, private val prefs: AppPrefs): KoinComponent {

    fun setupListCount(context: Context) = listCount(context)

    private fun listCount (context: Context, interval: Int = getInterval()) {

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