package com.craftrom.manager.utils.safetynet.presenter

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.craftrom.manager.R
import com.craftrom.manager.utils.safetynet.getRequestNonce
import com.craftrom.manager.utils.safetynet.model.BBSafetyNetApiModel
import com.craftrom.manager.utils.safetynet.model.OnFinishGetSafetyNetAPI

class DeviceFragmentPresenter: OnFinishGetSafetyNetAPI {

    private val bbSafetyNetApi: BBSafetyNetAPI = BBSafetyNetApiImpl()

    var nonceTV: TextView? = null
    var timestampMsTV: TextView? = null
    var apkPackageNameTV: TextView? = null
    var apkCertificateDigestSha256TV: TextView? = null
    var apkDigestSha256TV: TextView? = null
    var ctsProfileMatchTV: TextView? = null
    var basicIntegrityTV: TextView? = null
    var resultContainerLL: LinearLayoutCompat? = null

    fun checkIntegrity(context: Context) {
        if (bbSafetyNetApi.checkGooglePlayVersion(context)) {
            bbSafetyNetApi.compatibilityCheckRequest(getRequestNonce()!!,
                    context.getString(R.string.google_api_key),
                    context, this)
        }
    }

    override fun onFinish(bbSafetyNetApiModel: BBSafetyNetApiModel) {
        resultContainerLL?.visibility = View.VISIBLE
        nonceTV?.text = bbSafetyNetApiModel?.nonce
        timestampMsTV?.text = bbSafetyNetApiModel?.timestampMs.toString()
        apkPackageNameTV?.text = bbSafetyNetApiModel?.apkPackageName
        apkCertificateDigestSha256TV?.text = bbSafetyNetApiModel?.apkCertificateDigestSha256?.get(0)
        apkDigestSha256TV?.text = bbSafetyNetApiModel?.apkDigestSha256
        ctsProfileMatchTV?.text = bbSafetyNetApiModel?.ctsProfileMatch.toString()
        basicIntegrityTV?.text = bbSafetyNetApiModel?.basicIntegrity.toString()
    }
}