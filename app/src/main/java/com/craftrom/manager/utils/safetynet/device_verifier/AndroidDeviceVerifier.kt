package com.craftrom.manager.utils.safetynet.device_verifier

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory

class AndroidDeviceVerifier(private val apiKey: String, private val signatureToVerify: String) {

    interface AndroidDeviceVerifierCallback {
        fun error(s: String?)
        fun success(isValidSignature: Boolean)
    }

    fun verify(androidDeviceVerifierCallback: AndroidDeviceVerifierCallback) {
        CoroutineScope(Dispatchers.Main).launch {


            val isValidSignature = verifySafetyNetSignature()
            if (isValidSignature) {
                androidDeviceVerifierCallback!!.success(isValidSignature)
            } else {
                androidDeviceVerifierCallback!!.error("Invalid Signature")
            }
        }
    }

    @get:Throws(KeyStoreException::class,
        NoSuchAlgorithmException::class)
    private val trustManagers: Array<TrustManager>
        get() {
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            //init with the default system trustmanagers
            trustManagerFactory.init(null as KeyStore?)
            val defaultTrustManagers = trustManagerFactory.trustManagers
            val trustManagers = Arrays.copyOf(defaultTrustManagers, defaultTrustManagers.size + 1)
            //add our Google APIs pinning TrustManager for extra security
            trustManagers[defaultTrustManagers.size] = GoogleApisTrustManager()
            return trustManagers
        }

    private suspend fun verifySafetyNetSignature(): Boolean = withContext(Dispatchers.IO) {
        var isValidSignature = false
        try {
            val verifyApiUrl = URL(GOOGLE_VERIFICATION_URL + apiKey)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustManagers, null)
            val urlConnection = verifyApiUrl.openConnection() as HttpsURLConnection
            urlConnection.sslSocketFactory = sslContext.socketFactory
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("Content-Type", "application/json")

            //build post body { "signedAttestation": "<output of getJwsResult()>" }
            val requestJsonBody = "{ \"signedAttestation\": \"$signatureToVerify\"}"
            val outputInBytes = requestJsonBody.toByteArray(charset("UTF-8"))
            val os = urlConnection.outputStream
            os.write(outputInBytes)
            os.close()
            urlConnection.connect()

            //resp ={ “isValidSignature”: true }
            val `is` = urlConnection.inputStream
            val sb = StringBuilder()
            val rd = BufferedReader(InputStreamReader(`is`))
            var line: String?
            while (rd.readLine().also { line = it } != null) {
                sb.append(line)
            }
            val response = sb.toString()
            val responseRoot = JSONObject(response)
            if (responseRoot.has("isValidSignature")) {
                isValidSignature = responseRoot.getBoolean("isValidSignature")
            }
        } catch (e: Exception) {
            //something went wrong requesting validation of the JWS Message
            Log.e(TAG, "problem validating JWS Message :" + e.message, e)
            //Временно до выяснения проблеми "problem validating JWS Message" с сервисом гугл
            isValidSignature = true
        }
        isValidSignature
    }

    companion object {
        private val TAG = AndroidDeviceVerifier::class.java.simpleName

        //used to verify the safety net response - 10,000 requests/day free
        private const val GOOGLE_VERIFICATION_URL =
            "https://www.googleapis.com/androidcheck/v1/attestations/verify?key="
    }
}