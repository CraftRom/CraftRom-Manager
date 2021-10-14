package com.craftrom.manager.utils.safetynet.device_verifier

import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager


/**
 * Custom TrustManager to use SSL public key Pinning to verify connections to www.googleapis.com
 */
class GoogleApisTrustManager : X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        //NOT IMP
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        // validate all the GOOGLEAPIS_COM_PINS
        for (cert in chain) {
            val expected = validateCertificatePin(cert)
            if (!expected) {
                throw CertificateException("could not find a valid SSL public key pin for www.googleapis.com")
            }
        }
    }

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }

    @Throws(CertificateException::class)
    private fun validateCertificatePin(certificate: X509Certificate): Boolean {
        var digest: MessageDigest? = null
        digest = try {
            MessageDigest.getInstance("SHA1")
        } catch (e: NoSuchAlgorithmException) {
            throw CertificateException(e)
        }
        val pubKeyInfo = certificate.publicKey.encoded
        val pin = digest?.digest(pubKeyInfo)
        val pinAsBase64 = "sha1/" + Base64.encodeToString(pin, Base64.DEFAULT)
        for (validPin in GOOGLEAPIS_COM_PINS) {
            if (validPin.equals(pinAsBase64, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    companion object {
        //good candidate for DexGuard string encryption. Generated with https://github.com/scottyab/ssl-pin-generator
        private val GOOGLEAPIS_COM_PINS = arrayOf(
            "sha1/f2QjSla9GtnwpqhqreDLIkQNFu8=",
            "sha1/Q9rWMO5T+KmAym79hfRqo3mQ4Oo=",
            "sha1/wHqYaI2J+6sFZAwRfap9ZbjKzE4=")
    }
}
