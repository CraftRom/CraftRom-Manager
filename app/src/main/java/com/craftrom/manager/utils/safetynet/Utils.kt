package com.craftrom.manager.utils.safetynet

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.SecureRandom


fun checkGooglePlayVersionImpl(context: Context?): Boolean {
   return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)== ConnectionResult.SUCCESS
}

fun getRequestNonce(): ByteArray? {
    val nonceData = "Banco do Brasil Safety Net API Sample: " + System.currentTimeMillis()
    val random = SecureRandom()
    val byteStream = ByteArrayOutputStream()
    val bytes = ByteArray(24)
    random.nextBytes(bytes)
    try {
        byteStream.write(bytes)
        byteStream.write(nonceData.toByteArray())
    } catch (e: IOException) {
        return null
    }

    return byteStream.toByteArray()
}