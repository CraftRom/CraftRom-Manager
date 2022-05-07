package com.craftrom.manager.fragments.kernel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.activities.WebViewActivity
import com.craftrom.manager.fragments.kernel.adapter.KernelAdapter
import com.craftrom.manager.utils.Constants.Companion.KERNEL_NAME
import com.craftrom.manager.utils.Constants.Companion.showSnackMessage
import com.craftrom.manager.utils.DeviceSystemInfo
import com.craftrom.manager.utils.app.AppPrefs
import com.craftrom.manager.utils.updater.response.KernelUpdateResponse
import com.craftrom.manager.utils.updater.retrofit.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KernelFragment : Fragment(){
    private lateinit var model: TextView
    private lateinit var android_version: TextView
    private lateinit var security_patch: TextView
    private lateinit var kernel_version: TextView
    private lateinit var chidori_version: TextView
    private lateinit var board: TextView
    private lateinit var deviceButton: Button
    private lateinit var typeButton: Button
    private lateinit var image: ImageView
    private lateinit var addMaterialContainer: CoordinatorLayout
    lateinit var listV: RecyclerView

    private val prefs: AppPrefs by inject()

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
        chidori_version = root.findViewById(R.id.chidori_version)
        typeButton = root.findViewById(R.id.btnType)
        board = root.findViewById(R.id.board_version)
        deviceButton = root.findViewById(R.id.btnDevice)
        image = root.findViewById(R.id.prevImage)
        addMaterialContainer = root.findViewById(R.id.addMaterialContainerKernel)
        listV = root.findViewById(R.id.listV)

        deviceInfo()
        typeDialog()
        if (prefs.settings.kernelUpdate){        RetrofitClient().getService()
            .kernel(DeviceSystemInfo.deviceCode())
            .enqueue(object : Callback<List<KernelUpdateResponse>> {
                override fun onFailure(call: Call<List<KernelUpdateResponse>>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<List<KernelUpdateResponse>>,
                    response: Response<List<KernelUpdateResponse>>
                ) {
                    listV.adapter = KernelAdapter(context as FragmentActivity?, response.body())
                }

            })}

        return root
    }

    @SuppressLint("SetTextI18n")
    private fun deviceInfo() {
        val imageUrl = "https://raw.githubusercontent.com/CraftRom/craftrom.github.io/main/images/devices/small/${DeviceSystemInfo.deviceCode()}.png"
        val imageNull = R.drawable.placeholder
            Picasso.get()
                .load(imageUrl)
                .error(imageNull)
                .fit()
                .centerCrop()
                .into(image)

        model.text = "${DeviceSystemInfo.brand()} ${DeviceSystemInfo.model()} (${DeviceSystemInfo.device()})"
        android_version.text = "${DeviceSystemInfo.releaseVersion()} (API ${DeviceSystemInfo.apiLevel()})"
        board.text =" ${DeviceSystemInfo.board()} (${DeviceSystemInfo.hardware()}-${DeviceSystemInfo.arch()})"
        kernel_version.text = DeviceSystemInfo.kernelVersion()
        security_patch.text = Build.VERSION.SECURITY_PATCH
        deviceButton.text = DeviceSystemInfo.deviceCode()
        typeButton.text = prefs.settings.typeUpdate
        deviceButton.setOnClickListener {
            openDeviceInfo(DeviceSystemInfo.deviceCode())
        }

        if (prefs.settings.typeUpdate == "nightly"){
            typeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorFalse))
        } else {
            typeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTrue))
        }

        if (DeviceSystemInfo.chidoriName() != KERNEL_NAME)
        {
            chidori_version.text = resources.getString(R.string.snack_kernel_name_error)
            chidori_version.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorFalse))
            showSnackMessage(
                addMaterialContainer,
                getString(R.string.snack_kernel_name_error)
            )
        } else {
            chidori_version.text = "${DeviceSystemInfo.chidoriName()} (${DeviceSystemInfo.chidoriVersion()})"
        }
//        Log.e(TAG,"Current kernel version $kernelVersion")
//        Log.e(TAG,"Current kernel name $kernelName")
    }

    private fun openDeviceInfo(device: String) {
        val intent = Intent(context, WebViewActivity::class.java)
        val uri = "https://www.craft-rom.pp.ua/devices/$device/"
        intent.putExtra("link", uri)
        requireContext().startActivity(intent)
    }

    private fun typeDialog() {
        typeButton.setOnClickListener {
            // on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(requireContext(), R.style.ThemeBottomSheet)

            // on below line we are inflating a layout file which we have created.
            val card = LayoutInflater.from(context).inflate(R.layout.dialog_messages, null)

            val imageIcon = card.findViewById<ImageView>(R.id.messageIcon)
            val typeTitle = card.findViewById<TextView>(R.id.typeTitle)
            val typeInfo =card.findViewById<TextView>(R.id.typeInfo)

            if (prefs.settings.typeUpdate == "nightly"){
                typeTitle.text = resources.getString(R.string.settings_type_nightly)
                imageIcon.setImageResource(R.drawable.ic_outline_nightly)
                typeInfo.text = resources.getString(R.string.info_type_nightly)
            } else {
                imageIcon.setImageResource(R.drawable.ic_outline_stable)
                typeTitle.text = resources.getString(R.string.settings_type_stable)
                typeInfo.text = resources.getString(R.string.info_type_release)
            }
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialog.setCancelable(true)

            // on below line we are setting
            // content view to our view.
            dialog.setContentView(card)
            dialog.dismissWithAnimation = true
            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()

        }
    }


    companion object {

        // TODO: Customize parameter argument names
        var TYPE_UPDATE: String = ""
    }

}