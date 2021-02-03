package com.craftrom.manager.activities.into

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants


class IntoWelcomeActivity : AppCompatActivity() {
    var root: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_hello)
        val button: Button = findViewById<View>(R.id.next) as Button
        button.setOnClickListener {
            Constants.changeActivity<IntoFinalActivity>(this@IntoWelcomeActivity)
            finish()
        }

    }
}
