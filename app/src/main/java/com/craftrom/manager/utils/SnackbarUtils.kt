package com.craftrom.manager.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {
    @JvmStatic
    fun showSnackbar(v: View?, snackbarText: String?) {
        if (v == null || snackbarText == null) {
            return
        }
        Snackbar.make(v, snackbarText, Snackbar.LENGTH_LONG).show()
    }
}