package com.craftrom.manager.utils.app

import com.craftrom.manager.fragments.kernel.KernelFragment
import org.koin.core.component.KoinComponent

class TypeUtil(private val prefs: AppPrefs): KoinComponent {

    fun setupListCount() = listType()

    private fun listType (type: String = getType()) {

        when(prefs.settings.typeUpdate) {
            "nightly" -> {
                KernelFragment.TYPE_UPDATE = type
            }
            "release" -> {
                KernelFragment.TYPE_UPDATE = type
                }
            }
        }


    private fun getType() = when(prefs.settings.typeUpdate) {
        "nightly" -> "nightly"
        else -> "release"
    }
}