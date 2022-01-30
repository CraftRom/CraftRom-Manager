package com.craftrom.manager.utils.safetynet.device_verifier

import android.content.Context
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.craftrom.manager.utils.safetynet.Utils
import com.craftrom.manager.utils.safetynet.device_verifier.AndroidDeviceVerifier.AndroidDeviceVerifierCallback
import com.craftrom.manager.utils.safetynet.model.SafetyNetResponse
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnSuccessListener
import java.security.SecureRandom
import java.util.*


/**
 * Simple wrapper to request google Play services - SafetyNet test
 * Based on the code samples from https://developer.android.com/google/play/safetynet/start.html
 *
 *
 * Doesn't handle Google play services errors, just calls error on callback.
 *
 *
 */
class SafetyNetHelper(private var googleDeviceVerificationApiKey: String) {
    private var secureRandom: SecureRandom

    //used for local validation of API response payload
    private lateinit var requestNonce: ByteArray
    private var requestTimestamp: Long = 0
    private var packageName: String? = null
    private var apkCertificateDigests: List<String>? = null
    private var callback: SafetyNetWrapperCallback? = null

    /**
     * Gets the previous successful call to the safetynetAPI - this is mainly for debug purposes.
     *
     * @return
     */
    var lastResponse: SafetyNetResponse? = null
        private set

    /**
     * @param googleDeviceVerificationApiKey used to validate safety net response see https://developer.android.com/google/play/safetynet/start.html#verify-compat-check
     */
    fun SafetyNetHelper(googleDeviceVerificationApiKey: String?) {
        this.googleDeviceVerificationApiKey = googleDeviceVerificationApiKey!!
        assureApiKeysDefined()
        secureRandom = SecureRandom()
    }

    private fun assureApiKeysDefined() {
        if (TextUtils.isEmpty(googleDeviceVerificationApiKey)) {
            Log.w(
                TAG,
                "Google Device Verification Api Key not defined, cannot properly validate safety net response without it. See https://developer.android.com/google/play/safetynet/start.html#verify-compat-check"
            )
            throw IllegalArgumentException("safetyNetApiKey must be defined!")
        }
    }

    /**
     * Simple interface for handling SafetyNet API response
     */
    interface SafetyNetWrapperCallback {
        fun error(errorCode: Int, errorMessage: String?)
        fun success(ctsProfileMatch: Boolean, basicIntegrity: Boolean)
    }

    /**
     * Call the SafetyNet test to check if this device profile /ROM has passed the CTS test
     *
     * @param context                  used to build and init the GoogleApiClient
     * @param safetyNetWrapperCallback results and error handling
     */
    fun requestTest(context: Context?, safetyNetWrapperCallback: SafetyNetWrapperCallback?) {
        packageName = context!!.packageName
        callback = safetyNetWrapperCallback
        apkCertificateDigests = Utils.calcApkCertificateDigests(context, packageName)
        Log.d(TAG,
            "apkCertificateDigests:$apkCertificateDigests")
        runSafetyNetTest(context)
    }

    private fun runSafetyNetTest(context: Context) {
        Log.v(TAG, "running SafetyNet.API Test")
        requestNonce = generateOneTimeRequestNonce()
        requestTimestamp = System.currentTimeMillis()
        SafetyNet.getClient(context).attest(requestNonce, googleDeviceVerificationApiKey)
            .addOnSuccessListener(OnSuccessListener { attestationResponse ->
                val jwsResult = attestationResponse.jwsResult
                val response = parseJsonWebSignature(jwsResult)
                lastResponse = response

                //only need to validate the response if it says we pass
                if (!response!!.isCtsProfileMatch || !response.isBasicIntegrity) {
                    callback!!.success(response.isCtsProfileMatch, response.isBasicIntegrity)
                    return@OnSuccessListener
                } else {
                    //validate payload of the response
                    if (validateSafetyNetResponsePayload(response)) {
                        if (!TextUtils.isEmpty(googleDeviceVerificationApiKey)) {
                            //if the api key is set, run the AndroidDeviceVerifier
                            val androidDeviceVerifier = jwsResult?.let {
                                AndroidDeviceVerifier(
                                    googleDeviceVerificationApiKey, it
                                )
                            }
                            androidDeviceVerifier?.verify(object : AndroidDeviceVerifierCallback {
                                override fun error(s: String?) {
                                    callback!!.error(RESPONSE_ERROR_VALIDATING_SIGNATURE,
                                        "Response signature validation error: $s")
                                }

                                override fun success(isValidSignature: Boolean) {
                                    if (isValidSignature) {
                                        callback!!.success(response.isCtsProfileMatch,
                                            response.isBasicIntegrity)
                                    } else {
                                        callback!!.error(RESPONSE_FAILED_SIGNATURE_VALIDATION,
                                            "Response signature invalid")
                                    }
                                }
                            })
                        } else {
                            Log.w(TAG, "No google Device Verification ApiKey defined")
                            callback!!.error(RESPONSE_FAILED_SIGNATURE_VALIDATION_NO_API_KEY,
                                "No Google Device Verification ApiKey defined. Marking as failed. SafetyNet CtsProfileMatch: " + response.isCtsProfileMatch)
                        }
                    } else {
                        callback!!.error(RESPONSE_VALIDATION_FAILED,
                            "Response payload validation failed")
                    }
                }
            })
            .addOnFailureListener { e ->
                if (e is ApiException) {
                    val apiException = e
                    callback!!.error(RESPONSE_VALIDATION_FAILED,
                        "ApiException[" + apiException.statusCode + "] " + apiException.message)
                } else {
                    Log.d(TAG, "Error: " + e.message)
                    callback!!.error(RESPONSE_VALIDATION_FAILED,
                        "Response payload validation failed")
                }
            }
    }

    private fun validateSafetyNetResponsePayload(response: SafetyNetResponse?): Boolean {
        if (response == null) {
            Log.e(TAG, "SafetyNetResponse is null.")
            return false
        }

        //check the request nonce is matched in the response
        val requestNonceBase64 =
            Base64.encodeToString(requestNonce, Base64.DEFAULT).trim { it <= ' ' }
        if (requestNonceBase64 != response.nonce) {
            Log.e(TAG,
                "invalid nonce, expected = \"$requestNonceBase64\"")
            Log.e(TAG, "invalid nonce, response   = \"" + response.nonce + "\"")
            return false
        }
        if (!packageName.equals(response.apkPackageName, ignoreCase = true)) {
            Log.e(TAG,
                "invalid packageName, expected = \"$packageName\"")
            Log.e(TAG, "invalid packageName, response = \"" + response.apkPackageName + "\"")
            return false
        }
        val durationOfReq = response.timestampMs - requestTimestamp
        if (durationOfReq > MAX_TIMESTAMP_DURATION) {
            Log.e(TAG,
                "Duration calculated from the timestamp of response \"$durationOfReq \" exceeds permitted duration of \"$MAX_TIMESTAMP_DURATION\""
            )
            return false
        }
        if (!apkCertificateDigests!!.toTypedArray()
                .contentEquals(response.apkCertificateDigestSha256)
        ) {
            Log.e(TAG,
                "invalid apkCertificateDigest, local/expected = " + Arrays.asList(
                    apkCertificateDigests))
            Log.e(TAG,
                "invalid apkCertificateDigest, response = " + listOf(*response.apkCertificateDigestSha256)
            )
            return false
        }
        return true
    }

    private fun parseJsonWebSignature(jwsResult: String?): SafetyNetResponse? {
        if (jwsResult == null) {
            return null
        }
        //the JWT (JSON WEB TOKEN) is just a 3 base64 encoded parts concatenated by a . character
        val jwtParts = jwsResult.split("\\.".toRegex()).toTypedArray()
        return if (jwtParts.size == 3) {
            //we're only really interested in the body/payload
            val decodedPayload = String(Base64.decode(jwtParts[1], Base64.DEFAULT))
            SafetyNetResponse.parse(decodedPayload)
        } else {
            null
        }
    }

    private fun generateOneTimeRequestNonce(): ByteArray {
        val nonce = ByteArray(32)
        secureRandom.nextBytes(nonce)
        return nonce
    }

    companion object {
        private val TAG = SafetyNetHelper::class.java.simpleName
        const val SAFETY_NET_API_REQUEST_UNSUCCESSFUL = 999
        const val RESPONSE_ERROR_VALIDATING_SIGNATURE = 1000
        const val RESPONSE_FAILED_SIGNATURE_VALIDATION = 1002
        const val RESPONSE_FAILED_SIGNATURE_VALIDATION_NO_API_KEY = 1003
        const val RESPONSE_VALIDATION_FAILED = 1001

        /**
         * This is used to validate the payload response from the SafetyNet.API,
         * if it exceeds this duration, the response is considered invalid.
         */
        private const val MAX_TIMESTAMP_DURATION = 2 * 60 * 1000
    }

    /**
     * @param googleDeviceVerificationApiKey used to validate safety net response see https://developer.android.com/google/play/safetynet/start.html#verify-compat-check
     */
    init {
        assureApiKeysDefined()
        secureRandom = SecureRandom()
    }
}
