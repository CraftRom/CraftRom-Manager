package com.craftrom.manager.fragments.kernel

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
import com.craftrom.manager.utils.root.RootUtils


class KernelFragment : Fragment() {

    private lateinit var  kernel_name: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_kernel, container, false)
        kernel_name = root.findViewById(R.id.kernel_name)
        InitUI()
        return root

    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    private fun InitUI() {
        val versionString:String
        if (RootUtils.rootAccess()){
            versionString = Device.getKernelVersion(true)
        } else
        {
            versionString = RootUtils.runCommand("uname -a").toString()
        }

        val kernelString = "<b> Kernel: </b>" + versionString

        kernel_name.text = Html.fromHtml(kernelString, Html.FROM_HTML_MODE_LEGACY)

    }
}