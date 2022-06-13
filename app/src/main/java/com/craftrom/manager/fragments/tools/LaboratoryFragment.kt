package com.craftrom.manager.fragments.tools

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.services.FPS
import com.craftrom.manager.services.FPSTile
import com.craftrom.manager.utils.Utils
import com.google.android.material.card.MaterialCardView
import com.topjohnwu.superuser.Shell


class LaboratoryFragment : Fragment() {
    private lateinit var switchFpsMeter : SwitchCompat
    private lateinit var switchBatteryThermal : SwitchCompat
    private lateinit var perm: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_laboratory, container, false)
        val previewRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    it.data
                    // do whatever with the data in the callback
                }
            }
        switchFpsMeter = root.findViewById(R.id.switch_fps_meter)
        perm = root.findViewById(R.id.permission)
        // FPS On/Off Switch
        switchFpsMeter.setOnCheckedChangeListener { _, isChecked ->
            if (Utils.rootCheck(activity)) {
                perm.visibility = View.GONE
                if (isChecked) {
                    if (!Settings.canDrawOverlays(activity)) {
                        val packageName = context?.packageName
                        val intent = Intent(
                            "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                            Uri.parse("package:$packageName")
                        )
                        previewRequest.launch(intent)
                    } else {
                        FPSTile.Service.isRunning = true
                        context?.startForegroundService(Intent(activity, FPS::class.java))
                    }
                } else {
                    FPSTile.Service.isRunning = false
                    val intent = Intent(activity, FPS::class.java)
                    context?.stopService(intent)
                }
            } else{
                perm.visibility = View.VISIBLE
            }

        }

        perm.setOnClickListener {
            if (Shell.rootAccess()) if (Utils.hasStoragePM(requireContext())) {
                perm.visibility = View.GONE
            } else Utils.requestPM(this) else Toast.makeText(
                activity,
                R.string.err_no_pm_root,
                Toast.LENGTH_LONG
            ).show()
        }


        return root
    }
}