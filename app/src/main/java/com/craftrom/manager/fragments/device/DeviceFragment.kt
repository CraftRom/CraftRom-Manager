package com.craftrom.manager.fragments.device

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.utils.Device
import com.craftrom.manager.utils.root.CheckRoot
import com.craftrom.manager.utils.storage.isDiskEncrypted

class DeviceFragment : Fragment() {


    private lateinit var  kernel_name: TextView
    private lateinit var  oem_name: TextView
    private lateinit var disk_status: TextView
    private lateinit var root_status: TextView
    private lateinit var magisk_status: TextView
    var root: View? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_device, container, false)
        kernel_name = root.findViewById(R.id.kernel_name)
        oem_name = root.findViewById(R.id.oem_name)
        disk_status = root.findViewById(R.id.disk_status)
        root_status = root.findViewById(R.id.root_status)
        InitUI()
        return root
    }

     @Suppress("DEPRECATION")
     @SuppressLint("SetTextI18n")
     private fun InitUI() {
        val kernelString = "<b> Kernel: </b>" + Device.getKernelVersion(true)
        kernel_name.text = Html.fromHtml(kernelString, Html.FROM_HTML_MODE_LEGACY)
        oem_name.text = Device.getVendor().toString() + " " + Device.getModel()

        if (isDiskEncrypted) {
            disk_status.text = getString(R.string.disk_encrypted)
            disk_status.setTextColor(resources.getColor(R.color.colorTrue))
        } else {
            disk_status.text = getString(R.string.disk_not_encrypted)
            disk_status.setTextColor(resources.getColor(R.color.colorFalse))
        }

        if (CheckRoot.isDeviceRooted) {
            root_status.text = getString(R.string.device_rooted)
            root_status.setTextColor(resources.getColor(R.color.colorTrue))
        } else {
            root_status.text = getString(R.string.device_not_rooted)
            root_status.setTextColor(resources.getColor(R.color.colorFalse))
        }

    }
}