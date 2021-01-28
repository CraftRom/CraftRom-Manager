package com.craftrom.manager.fragments.home

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.craftrom.manager.R
import com.craftrom.manager.utils.Device

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var  kernel_name: TextView
    private lateinit var  oem_name: TextView
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
        InitUI()
        return root
    }
    private fun InitUI() {
        val kernelString = "<b> Kernel: </b>" + Device.getKernelVersion(true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            kernel_name.text = Html.fromHtml(kernelString, Html.FROM_HTML_MODE_LEGACY)
        } else {
            kernel_name.text = (Html.fromHtml(kernelString))
        }
        oem_name.setText(Device.getVendor().toString() + " " + Device.getModel())
    }
}