package com.craftrom.manager.utils

import com.craftrom.manager.utils.app.*
import com.kryptoprefs.preferences.KryptoBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mainModule = module {

    single { AppPrefs(get(), KryptoBuilder.nocrypt(get(), "${androidContext().packageName}_preferences")) }
    single { NewsUtil(get()) }
    single { TypeUtil(get()) }
    single { NotificationUtil(get()) }
    single { AlarmUtil(get(), get()) }
}