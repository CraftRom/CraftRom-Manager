package com.craftrom.manager.utils.safetynet.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BBSafetyNetApiModel {
    @SerializedName("nonce")
    @Expose
    var nonce: String? = null

    @SerializedName("timestampMs")
    @Expose
    var timestampMs: Long? = null

    @SerializedName("apkPackageName")
    @Expose
    var apkPackageName: String? = null

    @SerializedName("apkCertificateDigestSha256")
    @Expose
    var apkCertificateDigestSha256: List<String>? = null

    @SerializedName("apkDigestSha256")
    @Expose
    var apkDigestSha256: String? = null

    @SerializedName("ctsProfileMatch")
    @Expose
    var ctsProfileMatch: Boolean? = null

    @SerializedName("basicIntegrity")
    @Expose
    var basicIntegrity: Boolean? = null
}