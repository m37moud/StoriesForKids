package com.m37moud.responsivestories.ui.activities.started.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.m37moud.responsivestories.MainActivity
import com.m37moud.responsivestories.R
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        start.setOnClickListener {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
//            finish()
        }
    }
}