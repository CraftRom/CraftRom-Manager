package com.craftrom.manager.fragments.kernel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.activities.WebViewActivity
import com.craftrom.manager.utils.Constants.Companion.KERNEL_NAME
import com.craftrom.manager.utils.Constants.Companion.TAG
import com.craftrom.manager.utils.Constants.Companion.showSnackMessage
import com.craftrom.manager.utils.DeviceSystemInfo
import com.craftrom.manager.utils.ioScope
import com.craftrom.manager.utils.updater.repository.KernelUpdateRepository
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class KernelFragment : Fragment(){
    private lateinit var model: TextView
    private lateinit var android_version: TextView
    private lateinit var security_patch: TextView
    private lateinit var kernel_version: TextView
    private lateinit var board: TextView
    private lateinit var deviceGroup: Button
    private lateinit var image: ImageView
    private lateinit var addMaterialContainer: CoordinatorLayout

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
        deviceGroup = root.findViewById(R.id.btnDevice)
        image = root.findViewById(R.id.prevImage)
        addMaterialContainer = root.findViewById(R.id.addMaterialContainerKernel)

        deviceInfo()
        checkForSelfUpdate()

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
        deviceGroup.text = DeviceSystemInfo.deviceCode()

        deviceGroup.setOnClickListener {
            openDeviceInfo(DeviceSystemInfo.deviceCode())
        }

        val kernelVersion = DeviceSystemInfo.kernelVersion().substring(
            DeviceSystemInfo.kernelVersion().lastIndexOf(
                "."
            )
        ).substring(1, 4)

        val kernelName = DeviceSystemInfo.kernelVersion().substring(
            DeviceSystemInfo.kernelVersion().indexOf(
                "-"
            )
        ).substring(1, 8)

        if (kernelName != KERNEL_NAME)
        {
            showSnackMessage(
                addMaterialContainer,
                getString(R.string.snack_kernel_name_error)
            )
        } else {
            val text = resources.getString(R.string.snack_kernel_name, kernelName, kernelVersion)
            showSnackMessage(
                addMaterialContainer, text
            )
        }
//        Log.e(TAG,"Current kernel version $kernelVersion")
//        Log.e(TAG,"Current kernel name $kernelName")
    }

    private fun checkForSelfUpdate() = ioScope.launch {
        KernelUpdateRepository().checkForUpdatesAsync(requireContext()).await()
            .onFailure { Log.e(TAG, "Check for self update error.") }
    }
    private fun openDeviceInfo(device: String) {
        val intent = Intent(context, WebViewActivity::class.java)
        val uri = "https://www.craft-rom.pp.ua/devices/$device/"
        intent.putExtra("link", uri)
        requireContext().startActivity(intent)
    }
}