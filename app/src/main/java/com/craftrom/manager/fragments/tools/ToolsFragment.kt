package com.craftrom.manager.fragments.tools

import android.app.Activity.RESULT_OK
import android.app.ActivityManager
import android.content.Context
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
import com.craftrom.manager.utils.Constants.Companion.BATTERY_THERMAL_COOL_FILE
import com.craftrom.manager.utils.Constants.Companion.BATTERY_THERMAL_WARM_FILE
import com.craftrom.manager.utils.Utils
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.io.SuFile
import androidx.preference.Preference
import com.google.android.material.card.MaterialCardView
import com.topjohnwu.superuser.Shell
import android.app.Activity
import androidx.core.app.ServiceCompat.stopForeground


class ToolsFragment : Fragment() {
    private lateinit var switchFpsMeter : SwitchCompat
    private lateinit var switchBatteryThermal : SwitchCompat
    private lateinit var perm: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
        val previewRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    it.data
                    // do whatever with the data in the callback
                }
            }
        switchFpsMeter = root.findViewById(R.id.switch_fps_meter)
        switchBatteryThermal = root.findViewById(R.id.switch_battery_thermal)
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

        // Battery Thermal Switch Listener
        switchBatteryThermal.setOnCheckedChangeListener { _, isChecked ->
            if (Utils.rootCheck(activity)) {
                perm.visibility = View.GONE
                if (isChecked) {
                    ShellUtils.fastCmd("echo 450 > $BATTERY_THERMAL_COOL_FILE")
                    ShellUtils.fastCmd("echo 490 > $BATTERY_THERMAL_WARM_FILE")
                    return@setOnCheckedChangeListener
                }
                ShellUtils.fastCmd("echo 420 > $BATTERY_THERMAL_COOL_FILE")
                ShellUtils.fastCmd("echo 450 > $BATTERY_THERMAL_WARM_FILE")
            } else {
                perm.visibility = View.VISIBLE
            }
            getBatteryThermal()
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




    // Battery Thermal Switch State
    private fun getBatteryThermal() {
        if (!SuFile.open(BATTERY_THERMAL_COOL_FILE).exists() || !SuFile.open(
                BATTERY_THERMAL_WARM_FILE
            ).exists()
        ) {
            return
        }
        if (ShellUtils.fastCmd("cat $BATTERY_THERMAL_COOL_FILE")
                .toInt() > 420 || ShellUtils.fastCmd("cat $BATTERY_THERMAL_WARM_FILE").toInt() > 450
        ) {
            switchBatteryThermal.isChecked = true
        }
    }

    // Refresh Fragment
    override fun onStart() {
        super.onStart()

        getBatteryThermal()
    }

}