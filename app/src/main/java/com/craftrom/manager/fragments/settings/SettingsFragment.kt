package com.craftrom.manager.fragments.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.MovieGifView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private var gifViewPlayer: MovieGifView? = null
    private lateinit var updStartup: SwitchMaterial
    private lateinit var updChannel: SwitchMaterial

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val sharedPreferences = context?.getSharedPreferences("update", Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        gifViewPlayer  = view.findViewById(R.id.gifView)
        updStartup = view.findViewById(R.id.upd_start)
        updChannel = view.findViewById(R.id.beta_sign)

        gifViewPlayer?.setMovieAssets("emoji/thinking-emoji.gif")

        if(sharedPreferences != null) {

            updStartup.isChecked = sharedPreferences.getBoolean("startup", true)
            updChannel.isChecked = sharedPreferences.getString("channel", "stable").equals("test")

            updStartup.setOnClickListener {
                if (!(sharedPreferences.edit().putBoolean("startup", updStartup.isChecked).commit()))
                    updStartup.toggle()
            }

            updChannel.setOnClickListener {
                if(!(sharedPreferences.edit().putString("channel", if (sharedPreferences.getString("channel", "stable").equals("test")) "stable" else "test").commit()))
                    updChannel.toggle()
            }


        } else {
            Toast.makeText(context, "Failed to access Application's Data, Try reinstalling!", Toast.LENGTH_LONG).show()
            (activity as MainActivity).onBackPressed()
        }

        return view
    }

}
