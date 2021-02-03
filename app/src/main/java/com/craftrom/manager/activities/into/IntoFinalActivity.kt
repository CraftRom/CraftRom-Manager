package com.craftrom.manager.activities.into

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants


class IntoFinalActivity : AppCompatActivity() {
    var root: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_final)
        val button: Button = findViewById<View>(R.id.finish) as Button
        button.setOnClickListener {
            Constants.changeActivity<MainActivity>(this@IntoFinalActivity)
            finish()
        }

    }
}
