package com.craftrom.manager.utils.safetynet.presenter

import android.content.Context
import com.craftrom.manager.utils.safetynet.model.OnFinishGetSafetyNetAPI

interface BBSafetyNetAPI {
    fun checkGooglePlayVersion(context: Context): Boolean

    fun compatibilityCheckRequest(nonce: ByteArray, API_KEY: String, context: Context,
                                  onFinishGetSafetyNetAPI: OnFinishGetSafetyNetAPI
    )
}