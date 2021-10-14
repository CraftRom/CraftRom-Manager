package com.craftrom.manager.utils.safetynet

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import java.io.*
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import java.util.zip.CRC32
import java.util.zip.CheckedInputStream

/**
 * Useful but separate utils used by the safetynet helper
 */
object Utils {
    private val TAG = Utils::class.java.simpleName
    private const val SHA_256 = "SHA-256"

    /**
     * Created SHA256 of input
     * @param input (assumes UTF-8 string)
     * @return
     */
    fun hash(input: String): ByteArray? {
        if (!TextUtils.isEmpty(input)) {
            try {
                val inputBytes = input.toByteArray(charset("UTF-8"))
                return hash(inputBytes)
            } catch (e: UnsupportedEncodingException) {
                Log.e(TAG, "problem hashing \"" + input + "\" " + e.message, e)
            }
        }
        return null
    }

    /**
     * Created SHA256 of input
     * @param input
     * @return
     */
    private fun hash(input: ByteArray?): ByteArray? {
        if (input != null) {
            val digest: MessageDigest
            try {
                digest = MessageDigest.getInstance(SHA_256)
                val hashedBytes: ByteArray = input
                digest.update(hashedBytes, 0, hashedBytes.size)
                return hashedBytes
            } catch (e: NoSuchAlgorithmException) {
                Log.e(TAG, "problem hashing \"" + input + "\" " + e.message, e)
            }
        } else {
            Log.w(TAG, "hash called with null input byte[]")
        }
        return null
    }

    fun getSigningKeyFingerprint(ctx: Context): String? {
        var result: String? = null
        try {
            val certEncoded = getSigningKeyCertificate(ctx)
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(certEncoded)
            result = byte2HexFormatted(publicKey)
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return result
    }

    /**
     * Gets the encoded representation of the first signing cerificated used to sign current APK
     * @param ctx
     * @return
     */
    private fun getSigningKeyCertificate(ctx: Context): ByteArray? {
        try {
            val pm = ctx.packageManager
            val packageName = ctx.packageName
            val flags = PackageManager.GET_SIGNING_CERTIFICATES
            val packageInfo = pm.getPackageInfo(packageName, flags)
            val signatures: Array<Signature> = packageInfo.signingInfo.apkContentsSigners
            if (signatures.isNotEmpty()) {
                //takes just the first signature, TODO: handle multi signed apks
                val cert = signatures[0].toByteArray()
                val input: InputStream = ByteArrayInputStream(cert)
                val cf = CertificateFactory.getInstance("X509")
                val c = cf.generateCertificate(input) as X509Certificate
                return c.encoded
            }
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return null
    }

    private fun byte2HexFormatted(arr: ByteArray): String {
        val str = StringBuilder(arr.size * 2)
        for (i in arr.indices) {
            var h = Integer.toHexString(arr[i].toInt())
            val l = h.length
            if (l == 1) h = "0$h"
            if (l > 2) h = h.substring(l - 2, l)
            str.append(h.toUpperCase(Locale.ROOT))
            if (i < arr.size - 1) str.append(':')
        }
        return str.toString()
    }

    fun calcApkCertificateDigests(context: Context, packageName: String?): List<String> {
        val encodedSignatures: MutableList<String> = ArrayList()

        // Get signatures from package manager
        val pm = context.packageManager
        val packageInfo: PackageInfo = try {
            pm.getPackageInfo(packageName!!, PackageManager.GET_SIGNING_CERTIFICATES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return encodedSignatures
        }
        val signatures: Array<Signature> = packageInfo.signingInfo.apkContentsSigners

        // Calculate b64 encoded sha256 hash of signatures
        for (signature in signatures) {
            try {
                val md = MessageDigest.getInstance(SHA_256)
                md.update(signature.toByteArray())
                val digest = md.digest()
                encodedSignatures.add(Base64.encodeToString(digest, Base64.NO_WRAP))
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }
        return encodedSignatures
    }

    fun calcApkDigest(context: Context): String {
        val hashed2 = getApkFileDigest(context)
        return Base64.encodeToString(hashed2, Base64.NO_WRAP)
    }

    private fun getApkFileChecksum(context: Context): Long {
        val apkPath = context.packageCodePath
        var chksum: Long? = null
        try {
            // Open the file and build a CRC32 checksum.
            val fis = FileInputStream(File(apkPath))
            val chk = CRC32()
            val cis = CheckedInputStream(fis, chk)
            val buff = ByteArray(80)
            while (cis.read(buff) >= 0);
            chksum = chk.value
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return chksum!!
    }

    private fun getApkFileDigest(context: Context): ByteArray? {
        val apkPath = context.packageCodePath
        try {
            return getDigest(FileInputStream(apkPath), SHA_256)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
        return null
    }

    private const val BUFFER_SIZE = 2048
    @Throws(Throwable::class)
    fun getDigest(`in`: InputStream, algorithm: String?): ByteArray {
        val md = MessageDigest.getInstance(algorithm)
        try {
            val dis = DigestInputStream(`in`, md)
            val buffer = ByteArray(BUFFER_SIZE)
            while (dis.read(buffer) != -1) {
                //
            }
            dis.close()
        } finally {
            `in`.close()
        }
        return md.digest()
    }
}