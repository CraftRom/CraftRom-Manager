package com.craftrom.manager.utils.app

import android.content.Context
import com.kryptoprefs.context.KryptoContext
import com.kryptoprefs.preferences.KryptoPrefs

class AppPrefs(context: Context, prefs: KryptoPrefs): KryptoContext(prefs) {

    val settings = PreferenceFragmentPrefs(context, prefs.sharedPreferences())
    val selfUpdateCheck = long("selfUpdateCheck", 0)

}