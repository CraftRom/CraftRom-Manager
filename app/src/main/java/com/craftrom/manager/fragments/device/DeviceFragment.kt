package com.craftrom.manager.fragments.device


import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.Constants.Companion.TAG
import com.craftrom.manager.utils.Device
import com.craftrom.manager.utils.rom_checker.RomIdentifier.getRom
import com.craftrom.manager.utils.root.RootUtils
import com.craftrom.manager.utils.safetynet.device_verifier.SafetyNetHelper
import com.craftrom.manager.utils.safetynet.model.SafetyNetResponse
import com.craftrom.manager.utils.storage.isDiskEncrypted
import java.util.*


class DeviceFragment : Fragment(){
    private lateinit var  oem_name: TextView
    private lateinit var disk_status: TextView
    private lateinit var android_codename: TextView
    private lateinit var android_version: TextView
    private lateinit var rom_version: TextView
    private lateinit var cts: TextView
    private lateinit var basic_int: TextView
    private lateinit var advice_text : TextView
    private lateinit var safety_error: TextView
    private lateinit var error_note: TextView

    private lateinit var verify_safety_net: CardView

    private lateinit var advice_liner: LinearLayout
    private lateinit var cts_liner: LinearLayout
    private lateinit var basic_liner: LinearLayout
    private lateinit var safety_net_error: LinearLayout


    private lateinit var progressBar: ProgressBar
    private var safetyNetHelper: SafetyNetHelper? = null


    // TODO: Customize parameters
    private var columnCount = 1
    var listV: RecyclerView ?= null

    var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_device, container, false)
        oem_name = root.findViewById(R.id.oem_name)
        disk_status = root.findViewById(R.id.disk_status)
        android_codename = root.findViewById(R.id.android_codename)
        android_version = root.findViewById(R.id.android_version)
        rom_version = root.findViewById(R.id.rom_version)
        verify_safety_net = root.findViewById(R.id.safety_net)
        cts = root.findViewById(R.id.ctsProfileMatch)
        cts_liner = root.findViewById(R.id.cts_liner)
        basic_liner = root.findViewById(R.id.basic_liner)
        safety_error = root.findViewById(R.id.error_text)
        error_note= root.findViewById((R.id.error_note))
        safety_net_error = root.findViewById(R.id.safety_net_error)
        basic_int = root.findViewById(R.id.basicIntegrity)
        advice_text = root.findViewById(R.id.advice_text)
        advice_liner = root.findViewById(R.id.advice_liner)
        progressBar = root.findViewById(R.id.progressBar)
        listV = root.findViewById(R.id.listV)
        safetyNetHelper = SafetyNetHelper(Constants.API_KEY)

        verify_safety_net.setOnClickListener {
            runTest()
        }

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        InitUI()
        return root
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    private fun InitUI() {
        val rom = getRom()

        oem_name.text = Device.getVendor() + " " + Device.getModel()
        if (RootUtils.rootAccess()){
            if (isDiskEncrypted) {
                disk_status.text = getString(R.string.disk_encrypted)
                disk_status.setTextColor(resources.getColor(R.color.colorTrue))
            } else {
                disk_status.text = getString(R.string.disk_not_encrypted)
                disk_status.setTextColor(resources.getColor(R.color.colorFalse))
            }
        } else {
            disk_status.text = getString(R.string.disk_not_permission)
            disk_status.setTextColor(resources.getColor(R.color.colorPermission))
        }

        android_codename.text  = Device.getDeviceName()
        android_version.text = Device.getVersion()
        rom_version.text = rom.name + " " + rom.versionName
    }

    private fun runTest() {
        showLoading(true)
        Log.d(TAG, "SafetyNet start request")
        safetyNetHelper!!.requestTest(context, object : SafetyNetHelper.SafetyNetWrapperCallback {
            override fun error(errorCode: Int, errorMessage: String?) {
                showLoading(false)
                handleError(errorCode, errorMessage)
            }

            override fun success(ctsProfileMatch: Boolean, basicIntegrity: Boolean) {
                Log.d(TAG,
                    "SafetyNet req success: ctsProfileMatch:$ctsProfileMatch and basicIntegrity, $basicIntegrity")
                showLoading(false)
                updateUIWithSuccessfulResult(safetyNetHelper!!.lastResponse)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun handleError(errorCode: Int, errorMsg: String?) {
        showError(true)
        Log.e(TAG, errorMsg!!)
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
        safety_error.text = b.toString()
        error_note.text = "Error Msg:\n$errorMsg"
    }

    private fun updateUIWithSuccessfulResult(safetyNetResponse: SafetyNetResponse?) {
        val advice =
            if (safetyNetResponse!!.advice == null) "None availible" else safetyNetResponse.advice!!
        advice_text.text = advice
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
//        timestampTV.setText(sim.format(timeOfResponse))
//        packageNameTV.setText(safetyNetResponse.apkPackageName)
    }


    private fun showLoading(show: Boolean) {
        if (show) {
            cts_liner.visibility = View.GONE
            basic_liner.visibility = View.GONE
            advice_liner.visibility = View.GONE
            safety_net_error.visibility =View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            cts_liner.visibility = View.VISIBLE
            basic_liner.visibility = View.VISIBLE
            advice_liner.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun showError(show_error: Boolean) {
        if (show_error) {
            cts_liner.visibility = View.GONE
            basic_liner.visibility = View.GONE
            advice_liner.visibility = View.GONE
            safety_net_error.visibility = View.VISIBLE
        } else {
            cts_liner.visibility = View.VISIBLE
            basic_liner.visibility = View.VISIBLE
            advice_liner.visibility = View.VISIBLE
            safety_net_error.visibility = View.GONE
        }
    }



    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            DeviceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}