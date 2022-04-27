package com.craftrom.manager.fragments.kernel

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.utils.DeviceSystemInfo
import com.squareup.picasso.Picasso

class KernelFragment : Fragment(){
    private lateinit var model: TextView
    private lateinit var android_version: TextView
    private lateinit var security_patch: TextView
    private lateinit var kernel_version: TextView
    private lateinit var board: TextView
    private lateinit var image: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_kernel, container, false)
        model = root.findViewById((R.id.kernel_model))
        android_version = root.findViewById((R.id.kernel_android))
        security_patch = root.findViewById(R.id.kernel_security_patch)
        kernel_version = root.findViewById(R.id.kernel_version)
        board = root.findViewById(R.id.board_version)
        image = root.findViewById(R.id.prevImage)

        deviceInfo()
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun deviceInfo() {
        val imageUrl = "https://raw.githubusercontent.com/CraftRom/craftrom.github.io/main/images/devices/small/${DeviceSystemInfo.product()}.png"
        val imageNull = R.drawable.placeholder
            Picasso.get()
                .load(imageUrl)
                .error(imageNull)
                .fit()
                .centerCrop()
                .into(image)

        model.text = "${DeviceSystemInfo.brand()} ${DeviceSystemInfo.model()} (${DeviceSystemInfo.product()})"
        android_version.text = "${DeviceSystemInfo.releaseVersion()} (API ${DeviceSystemInfo.apiLevel()})"
        board.text =" ${DeviceSystemInfo.board()} (${DeviceSystemInfo.hardware()}-${DeviceSystemInfo.arch()})"
        kernel_version.text = DeviceSystemInfo.kernelVersion()
        security_patch.text = Build.VERSION.SECURITY_PATCH
    }
}