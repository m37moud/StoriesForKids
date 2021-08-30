package com.m37moud.responsivestories.ui.activities.started.onboarding

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.m37moud.responsivestories.MainActivity
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.util.Constants
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
            start.isClickable =false
//            finish()
        }

//        val colors = IntArray(2)
//        colors[0] = Constants.getRandomColor()
//        colors[1] = Constants.getRandomColor()
//
//
//        val gd = GradientDrawable(
//            GradientDrawable.Orientation.TOP_BOTTOM, colors
//        )
//
//        gd.gradientType = GradientDrawable.RADIAL_GRADIENT
//        gd.gradientRadius = 300f
//        gd.cornerRadius = 0f
//        main_FrameLayout.background = gd
    }

    override fun onResume() {
        start.isClickable =true

        super.onResume()
    }

    override fun onStart() {
        start.isClickable =true
        super.onStart()
    }
}