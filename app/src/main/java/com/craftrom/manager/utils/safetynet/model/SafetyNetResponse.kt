package com.craftrom.manager.utils.safetynet.model

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * SafetyNet API payload Response (once unencoded from JSON Web token)
 *
 *
 * {
 * "nonce": "iBnt4sI4KCA5Vqh7yDxzUVJYxBYUIQG396Wgmu6lA/Y=",
 * "timestampMs": 1432658018093,
 * "apkPackageName": "com.scottyab.safetynet.sample",
 * "apkDigestSha256": "WN2ADq4LZvMsd0CFBIkGRl8bn3mRKIppCmnqsrJzUJg=",
 * "ctsProfileMatch": false,
 * "basicIntegrity": false,
 * "extension": "CY+oATrcJ6Cr",
 * "apkCertificateDigestSha256": ["Yao6w7Yy7/ab2bNEygMbXqN9+16j8mLKKTCsUcU3Mzw="]
 * "advice": "LOCK_BOOTLOADER,RESTORE_TO_FACTORY_ROM"
 * }
 *
 *
 */
class SafetyNetResponse  //forces the parse()
private constructor() {
    /**
     * @return BASE64 encoded
     */
    var nonce: String? = null
        private set
    var timestampMs: Long = 0
        private set

    /**
     * @return com.package.name.of.requesting.app
     */
    var apkPackageName: String? = null
        private set

    /**
     * SHA-256 hash of the certificate used to sign requesting app
     *
     * @return BASE64 encoded
     */
    lateinit var apkCertificateDigestSha256: Array<String?>
        private set

    /**
     * SHA-256 hash of the app's APK
     *
     * Google Play since March 2018 adds a small amount of metadata to all apps which makes this apk validation less useful.
     *
     * @return BASE64 encoded
     */
    @get:Deprecated("")
    var apkDigestSha256: String? = null
        private set

    /**
     * If the value of "ctsProfileMatch" is true, then the profile of the device running your app matches the profile of a device that has passed Android compatibility testing.
     *
     * @return
     */
    var isCtsProfileMatch = false
        private set

    /**
     * If the value of "basicIntegrity" is true, then the device running your app likely wasn't tampered with, but the device has not necessarily passed Android compatibility testing.
     *
     * @return
     */
    var isBasicIntegrity = false
        private set

    /**
     * Advice for passing future checks
     *
     * @return
     */
    var advice: String? = null
        private set

    override fun toString(): String {
        return "SafetyNetResponse{" +
                "nonce='" + nonce + '\'' +
                ", timestampMs=" + timestampMs +
                ", apkPackageName='" + apkPackageName + '\'' +
                ", apkCertificateDigestSha256=" + Arrays.toString(apkCertificateDigestSha256) +
                ", apkDigestSha256='" + apkDigestSha256 + '\'' +
                ", ctsProfileMatch=" + isCtsProfileMatch +
                ", basicIntegrity=" + isBasicIntegrity +
                ", advice=" + advice +
                '}'
    }

    companion object {
        private val TAG = SafetyNetResponse::class.java.simpleName

        /**
         * Parse the JSON string into populated SafetyNetResponse object
         *
         * @param decodedJWTPayload JSON String (always a json string according to JWT spec)
         * @return populated SafetyNetResponse
         */
        fun parse(decodedJWTPayload: String): SafetyNetResponse? {
            Log.d(TAG,
                "decodedJWTPayload json:$decodedJWTPayload")
            val response = SafetyNetResponse()
            try {
                val root = JSONObject(decodedJWTPayload)
                if (root.has("nonce")) {
                    response.nonce = root.getString("nonce")
                }
                if (root.has("apkCertificateDigestSha256")) {
                    val jsonArray = root.getJSONArray("apkCertificateDigestSha256")
                    val certDigests = arrayOfNulls<String>(jsonArray.length())
                    for (i in 0 until jsonArray.length()) {
                        certDigests[i] = jsonArray.getString(i)
                    }
                    response.apkCertificateDigestSha256 = certDigests
                }
                if (root.has("apkDigestSha256")) {
                    response.apkDigestSha256 = root.getString("apkDigestSha256")
                }
                if (root.has("apkPackageName")) {
                    response.apkPackageName = root.getString("apkPackageName")
                }
                if (root.has("basicIntegrity")) {
                    response.isBasicIntegrity = root.getBoolean("basicIntegrity")
                }
                if (root.has("ctsProfileMatch")) {
                    response.isCtsProfileMatch = root.getBoolean("ctsProfileMatch")
                }
                if (root.has("timestampMs")) {
                    response.timestampMs = root.getLong("timestampMs")
                }
                if (root.has("advice")) {
                    response.advice = root.getString("advice")
                }
                return response
            } catch (e: JSONException) {
                Log.e(TAG, "problem parsing decodedJWTPayload:" + e.message, e)
            }
            return null
        }
    }
}
