package com.craftrom.manager.fragments.device

import android.annotation.SuppressLint
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
import com.craftrom.manager.utils.Constants.Companion.API_KEY
import com.craftrom.manager.utils.Constants.Companion.TAG
import com.craftrom.manager.utils.Device
import com.craftrom.manager.utils.rom_checker.RomIdentifier.getRom
import com.craftrom.manager.utils.root.RootUtils
import com.craftrom.manager.utils.safetynet.device_verifier.SafetyNetHelper
import com.craftrom.manager.utils.safetynet.device_verifier.SafetyNetWrapperCallback
import com.craftrom.manager.utils.storage.isDiskEncrypted


class DeviceFragment : Fragment(){
    private lateinit var  oem_name: TextView
    private lateinit var disk_status: TextView
    private lateinit var android_codename: TextView
    private lateinit var android_version: TextView
    private lateinit var rom_version: TextView
    private lateinit var verify_safety_net: CardView
    private lateinit var cts: TextView
    private lateinit var basic_int: TextView
    private lateinit var safety_error: TextView
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
        safety_net_error = root.findViewById(R.id.safety_net_error)
        basic_int = root.findViewById(R.id.basicIntegrity)
        progressBar = root.findViewById(R.id.progressBar)
        listV = root.findViewById(R.id.listV)
        safetyNetHelper = SafetyNetHelper(API_KEY)

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
        showError(false)
        showLoading(true)
        Log.d(TAG, ":Device SafetyNet start request")
        safetyNetHelper?.startSafetyNetTest(
            requireContext(),
            object : SafetyNetWrapperCallback {
                override fun error(errorCode: Int, errorMessage: String?) {
                    showLoading(false)
                    handleError(errorCode, errorMessage!!)
                }

                override fun success(ctsProfileMatch: Boolean, basicIntegrity: Boolean) {
                    val cts_result = "$ctsProfileMatch"
                    val basic_result = "$basicIntegrity"
                    Log.d(TAG, ":Device SafetyNet req success: \n" +
                            "ctsProfileMatch : $ctsProfileMatch \n" +
                            "basicIntegrity : $basicIntegrity")
                    showLoading(false)
                    if (ctsProfileMatch){
                        cts.setTextColor(ContextCompat.getColor(context!!, R.color.colorTrue))
                    } else {
                        cts.setTextColor(ContextCompat.getColor(context!!, R.color.colorFalse))
                    }
                    if (basicIntegrity){
                        basic_int.setTextColor(ContextCompat.getColor(context!!, R.color.colorTrue))
                    } else {
                        basic_int.setTextColor(ContextCompat.getColor(context!!, R.color.colorFalse))
                    }

                    cts.text = cts_result
                    basic_int.text = basic_result
                }
            })
    }

    private fun handleError(errorCode: Int, errorMsg: String) {
        Log.e(TAG, errorMsg)
        showError(true)
        val b = StringBuilder()
        when (errorCode) {
            SafetyNetHelper.ERROR_SAFETY_NET_API_REQUEST_UNSUCCESSFUL -> {
                b.append("SafetyNet request failed\n")
                b.append("(This could be a networking issue.)\n")
            }
            SafetyNetHelper.ERROR_RESPONSE_ERROR_VALIDATING_SIGNATURE -> {
                b.append("SafetyNet request: success\n")
                b.append("Response signature validation: error\n")
            }
            SafetyNetHelper.ERROR_RESPONSE_FAILED_SIGNATURE_VALIDATION -> {
                b.append("SafetyNet request: success\n")
                b.append("Response signature validation: fail\n")
            }
            SafetyNetHelper.ERROR_RESPONSE_VALIDATION_FAILED -> {
                b.append("SafetyNet request: success\n")
                b.append("Response validation: fail\n")
            }
            else -> {
                b.append("SafetyNet request failed\n")
                b.append("(This could be a networking issue.)\n")
            }
        }
        safety_error.text = b.toString() + "\n" + errorMsg
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            cts_liner.visibility = View.GONE
            basic_liner.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            cts_liner.visibility = View.VISIBLE
            basic_liner.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun showError(show_error: Boolean) {
        if (show_error) {
            cts_liner.visibility = View.GONE
            basic_liner.visibility = View.GONE
            safety_net_error.visibility = View.VISIBLE
        } else {
            cts_liner.visibility = View.VISIBLE
            basic_liner.visibility = View.VISIBLE
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