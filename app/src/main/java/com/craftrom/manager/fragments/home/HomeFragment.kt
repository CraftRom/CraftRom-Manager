package com.craftrom.manager.fragments.home

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.craftrom.manager.R
import com.craftrom.manager.utils.Device
import com.craftrom.manager.utils.root.CheckRoot
import com.craftrom.manager.utils.security.MagiskDetector
import com.craftrom.manager.utils.storage.isDiskEncrypted
import javax.inject.Inject

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
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
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        kernel_name = root.findViewById(R.id.kernel_name)
        oem_name = root.findViewById(R.id.oem_name)
        disk_status = root.findViewById(R.id.disk_status)
        root_status = root.findViewById(R.id.root_status)
        InitUI()
        return root
    }

     fun InitUI() {
        val kernelString = "<b> Kernel: </b>" + Device.getKernelVersion(true)
        kernel_name.text = Html.fromHtml(kernelString, Html.FROM_HTML_MODE_LEGACY)
        oem_name.setText(Device.getVendor().toString() + " " + Device.getModel())

        if (isDiskEncrypted) {
            disk_status.text = getString(R.string.disk_encrypted)
        } else {
            disk_status.text = getString(R.string.disk_not_encrypted)
        }

        if (CheckRoot.isDeviceRooted) {
            root_status.text = getString(R.string.device_rooted)
        } else {
            root_status.text = getString(R.string.device_not_rooted)
        }

    }
}