package com.craftrom.manager.fragments.safety

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build.DEVICE
import android.os.Build.MODEL
import android.os.Build.VERSION.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.safetynet.device_verifier.SafetyNetHelper
import com.craftrom.manager.utils.safetynet.model.SafetyNetResponse
import java.util.*

class SafetyFragment : Fragment(){
    private lateinit var cts: TextView
    private lateinit var basic_int: TextView
    private lateinit var advice_text : TextView
    private lateinit var date: TextView
    private lateinit var eva_type: TextView
    private lateinit var digest: TextView
    private lateinit var model: TextView
    private lateinit var android_version: TextView
    private lateinit var security_patch: TextView
    private lateinit var error_txt: TextView

    private lateinit var btnCheck: Button

    private lateinit var verify_safety_net: CardView
    private lateinit var advice: CardView
    private lateinit var error: CardView

    private var safetyNetHelper: SafetyNetHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_safety, container, false)
        verify_safety_net = root.findViewById(R.id.safety_net)
        advice = root.findViewById(R.id.advice)
        error = root.findViewById(R.id.error)

        btnCheck = root.findViewById(R.id.btnCheck)
        cts = root.findViewById(R.id.safety_cts)
        basic_int = root.findViewById(R.id.safety_basic)
        advice_text = root.findViewById(R.id.txtAdvice)
        eva_type = root.findViewById(R.id.safety_type)
        date = root.findViewById((R.id.safety_time))
        digest = root.findViewById((R.id.safety_digest))
        model = root.findViewById((R.id.safety_model))
        android_version = root.findViewById((R.id.safety_android))
        security_patch = root.findViewById(R.id.safety_security_patch)
        error_txt = root.findViewById(R.id.txtError)

        safetyNetHelper = SafetyNetHelper(Constants.API_KEY)
        deviceInfo()
        btnCheck.setOnClickListener {
            runTest()
        }

        return root
    }

    private fun runTest() {
        Log.d(Constants.TAG, "SafetyNet start request")
        safetyNetHelper!!.requestTest(context, object : SafetyNetHelper.SafetyNetWrapperCallback {
            override fun error(errorCode: Int, errorMessage: String?) {
   //             showLoading(false)
                verify_safety_net.visibility = View.GONE
                handleError(errorCode, errorMessage)
            }

            override fun success(ctsProfileMatch: Boolean, basicIntegrity: Boolean) {
                Log.d(
                    Constants.TAG,
                    "SafetyNet req success: ctsProfileMatch:$ctsProfileMatch and basicIntegrity, $basicIntegrity")
  //              showLoading(false)
                verify_safety_net.visibility = View.VISIBLE
                showError(false)
                updateUIWithSuccessfulResult(safetyNetHelper!!.lastResponse)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun deviceInfo() {
        model.text = "$MODEL ($DEVICE)"
        android_version.text = "$RELEASE (API $SDK_INT)"
        security_patch.text = SECURITY_PATCH
    }

    @SuppressLint("SetTextI18n")
    private fun handleError(errorCode: Int, errorMsg: String?) {
    showError(true)
        Log.e(Constants.TAG, errorMsg!!)
        val b = StringBuilder()
        when (errorCode) {
            SafetyNetHelper.SAFETY_NET_API_REQUEST_UNSUCCESSFUL -> {
                b.append("SafetyNet request failed\n")
                b.append("(This could be a networking issue.)\n")
            }
            SafetyNetHelper.RESPONSE_ERROR_VALIDATING_SIGNATURE -> {
                b.append("SafetyNet request: success\n")
                b.append("Response signature validation: error\n")
            }
            SafetyNetHelper.RESPONSE_FAILED_SIGNATURE_VALIDATION -> {
                b.append("SafetyNet request: success\n")
                b.append("Response signature validation: fail\n")
            }
            SafetyNetHelper.RESPONSE_VALIDATION_FAILED -> {
                b.append("SafetyNet request: success\n")
                b.append("Response validation: fail\n")
            }
            else -> {
                b.append("SafetyNet request failed\n")
                b.append("(This could be a networking issue.)\n")
            }
        }
        error_txt.text = "$b\nError Msg:\n$errorMsg"
  //      error_note.text = "Error Msg:\n$errorMsg"
    }

    private fun updateUIWithSuccessfulResult(safetyNetResponse: SafetyNetResponse?) {
                if (safetyNetResponse!!.advice == "LOCK_BOOTLOADER")
                {
                    advice.visibility = View.VISIBLE
                    advice_text.setText(R.string.advice_bootloader)
                }
                if (safetyNetResponse.advice == "RESTORE_TO_FACTORY_ROM")
                {
                    advice.visibility = View.VISIBLE
                    advice_text.setText(R.string.advice_factory_rom)
                }
        cts.text = safetyNetResponse.isCtsProfileMatch.toString()
        basic_int.text =safetyNetResponse.isBasicIntegrity.toString()
        if (safetyNetResponse.isCtsProfileMatch ) {
            cts.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTrue))
        } else {
            cts.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorFalse))
        }
        if (safetyNetResponse.isBasicIntegrity ) {
            basic_int.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTrue))
        } else {
            basic_int.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorFalse))
        }

        val sim = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val timeOfResponse = Date(safetyNetResponse.timestampMs)
        date.text = sim.format(timeOfResponse)
        eva_type.text = safetyNetResponse.evaluationType
        digest.text = safetyNetResponse.apkDigestSha256
    }



   private fun showError(show_error: Boolean) {
        if (show_error) {
            error.visibility = View.VISIBLE
        } else {
            error.visibility = View.GONE
        }
    }
}