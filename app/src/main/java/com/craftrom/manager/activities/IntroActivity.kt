package com.craftrom.manager.activities;

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        findViewById<Button>(R.id.ok).setOnClickListener {
            Constants.changeActivity<MainActivity>(this)
        }
    }
}
