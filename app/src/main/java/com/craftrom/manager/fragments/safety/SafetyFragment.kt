package com.craftrom.manager.fragments.safety

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.safetynet.device_verifier.SafetyNetHelper
import com.craftrom.manager.utils.safetynet.model.SafetyNetResponse
import java.util.*

class SafetyFragment : Fragment(){
    private lateinit var cts: TextView
    private lateinit var basic_int: TextView
    private lateinit var advice_text : TextView
    private lateinit var evaluationType: TextView
    private lateinit var error_txt: TextView
    private lateinit var date: TextView
    private lateinit var packageName: TextView

    private lateinit var btnCheck: Button

    private lateinit var safetyLogo: ImageView

    private lateinit var verify_safety_net: CardView
    private lateinit var advice: CardView
    private var safetyNetHelper: SafetyNetHelper? = null

    var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_safety, container, false)
        verify_safety_net = root.findViewById(R.id.safety_net)
        btnCheck = root.findViewById(R.id.btnCheck)
        cts = root.findViewById(R.id.safety_cts)
        basic_int = root.findViewById(R.id.safety_basic)
        advice_text = root.findViewById(R.id.txtAdvice)
        evaluationType = root.findViewById(R.id.evaluationType)
        error_txt = root.findViewById(R.id.txtError)
        safetyLogo = root.findViewById(R.id.safetyLogo)
        advice = root.findViewById(R.id.advice)
        date = root.findViewById(R.id.txtDate)
        packageName = root.findViewById((R.id.txtApk))
        safetyNetHelper = SafetyNetHelper(Constants.API_KEY)

        btnCheck.setOnClickListener {
            runTest()
        }

        return root
    }

    private fun runTest() {
  //      showLoading(true)
        Log.d(Constants.TAG, "SafetyNet start request")
        safetyNetHelper!!.requestTest(context, object : SafetyNetHelper.SafetyNetWrapperCallback {
            override fun error(errorCode: Int, errorMessage: String?) {
   //             showLoading(false)
                handleError(errorCode, errorMessage)
            }

            override fun success(ctsProfileMatch: Boolean, basicIntegrity: Boolean) {
                Log.d(
                    Constants.TAG,
                    "SafetyNet req success: ctsProfileMatch:$ctsProfileMatch and basicIntegrity, $basicIntegrity")
  //              showLoading(false)
                showError(false)
                updateUIWithSuccessfulResult(safetyNetHelper!!.lastResponse)
            }
        })
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
         evaluationType.text = "$b\nError Msg:\n$errorMsg"
  //      error_note.text = "Error Msg:\n$errorMsg"
    }

    private fun updateUIWithSuccessfulResult(safetyNetResponse: SafetyNetResponse?) {
                if (safetyNetResponse!!.advice == "LOCK_BOOTLOADER")
                {
                    advice.visibility = View.VISIBLE
                    advice_text.text = "The user should lock their device's bootloader."
                }
                if (safetyNetResponse.advice == "RESTORE_TO_FACTORY_ROM")
                {
                    advice.visibility = View.VISIBLE
                    advice_text.text = "The user should restore their device to a clean factory ROM."
                }

        evaluationType.text =safetyNetResponse.evaluationType
        cts.text = safetyNetResponse.isCtsProfileMatch.toString()
        basic_int.text =safetyNetResponse.isBasicIntegrity.toString()
        if (safetyNetResponse.isCtsProfileMatch ) {
            cts.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTrue))
            safetyLogo.setImageResource(R.drawable.shield)
        } else {
            cts.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorFalse))
            safetyLogo.setImageResource(R.drawable.shield_error)
        }
        if (safetyNetResponse.isBasicIntegrity ) {
            basic_int.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTrue))
        } else {
            basic_int.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorFalse))
        }

        val sim = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val timeOfResponse = Date(safetyNetResponse.timestampMs)
        date.setText(sim.format(timeOfResponse))
        packageName.setText(safetyNetResponse.apkPackageName)
    }



   private fun showError(show_error: Boolean) {
        if (show_error) {
            safetyLogo.setImageResource(R.drawable.shield_error)
            error_txt.text = "Failed"
            error_txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorFalse))
        } else {
            error_txt.text = "Passed"
            error_txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTrue))
        }
    }
}