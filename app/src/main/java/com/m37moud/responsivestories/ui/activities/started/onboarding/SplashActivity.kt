package com.m37moud.responsivestories.ui.activities.started.onboarding

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.m37moud.responsivestories.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        // It is deprecated in the API level 30. I will update you with the alternate solution soon.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Adding the handler to after the a task after some delay.
        // It is deprecated in the API level 30.
        Handler().postDelayed(
            {

                // If the user is logged in once and did not logged out manually from the app.
                // So, next time when the user is coming into the app user will be redirected to MainScreen.
                // If user is not logged in or logout manually then user will  be redirected to the Login screen as usual.

                // Get the current logged in user id
                startActivity(Intent(this@SplashActivity, StartActivity::class.java))
                if(onBoardingFinished()) {
                    // Launch dashboard screen.

                } else {
                    // Launch the Login Activity
                    startActivity(Intent(this@SplashActivity, ViewPagerActivity::class.java))
                }
                finish() // Call this when your activity is done and should be closed.
            },
            2500
        ) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.
    }


    private fun onBoardingFinished(): Boolean{
        val sharedPref = this@SplashActivity.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}