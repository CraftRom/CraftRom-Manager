package com.craftrom.manager.utils.safetynet.device_verifier

import android.os.AsyncTask
import android.util.Log
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


/**
 * Validates the result with Android Device Verification API.
 *
 *
 * Note: This only validates that the provided JWS (JSON Web Signature) message was received from the actual SafetyNet service.
 * It does *not* verify that the payload data matches your original compatibility check request.
 * POST to https://www.googleapis.com/androidcheck/v1/attestations/verify?key=<your API key>
</your> *
 *
 * More info see {link https://developer.android.com/google/play/safetynet/start.html#verify-compat-check}
 */
class AndroidDeviceVerifier(private val apiKey: String, private val signatureToVerify: String) {
    private var callback: AndroidDeviceVerifierCallback? = null

    interface AndroidDeviceVerifierCallback {
        fun error(s: String?)
        fun success(isValidSignature: Boolean)
    }

    fun verify(androidDeviceVerifierCallback: AndroidDeviceVerifierCallback?) {
        callback = androidDeviceVerifierCallback
    }//init with the default system trustmanagers
    //add our Google APIs pinning TrustManager for extra security
    /**
     * Provide the trust managers for the URL connection. By Default this uses the system defaults plus the GoogleApisTrustManager (SSL pinning)
     *
     * @return array of TrustManager including system defaults plus the GoogleApisTrustManager (SSL pinning)
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     */
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

    private abstract inner class AndroidDeviceVerifierTask :
        AsyncTask<Void?, Void?, Boolean>() {
        private var error: Exception? = null
        protected fun doInBackground(vararg params: Void): Boolean {

            //Log.d(TAG, "signatureToVerify:" + signatureToVerify);
            try {
                val verifyApiUrl = URL(GOOGLE_VERIFICATION_URL + apiKey)
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustManagers, null)
                val urlConnection = verifyApiUrl.openConnection() as HttpsURLConnection
                urlConnection.sslSocketFactory = sslContext.socketFactory
                urlConnection.requestMethod = "POST"
                urlConnection.setRequestProperty("Content-Type", "application/json")

                //build post body { "signedAttestation": "<output of getJwsResult()>" }
                val requestJsonBody =
                    "{ \"signedAttestation\": \"$signatureToVerify\"}"
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
                    return responseRoot.getBoolean("isValidSignature")
                }
            } catch (e: Exception) {
                //something went wrong requesting validation of the JWS Message
                error = e
                Log.e(TAG, "problem validating JWS Message :" + e.message, e)
                return false
            }
            return false
        }

        override fun onPostExecute(aBoolean: Boolean) {
            if (error != null) {
                callback!!.error(error!!.message)
            } else {
                callback!!.success(aBoolean)
            }
        }
    }

    companion object {
        private val TAG = AndroidDeviceVerifier::class.java.simpleName

        //used to verify the safety net response - 10,000 requests/day free
        private const val GOOGLE_VERIFICATION_URL =
            "https://www.googleapis.com/androidcheck/v1/attestations/verify?key="
    }
}
