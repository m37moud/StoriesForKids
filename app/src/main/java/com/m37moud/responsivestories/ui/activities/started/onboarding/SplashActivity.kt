package com.m37moud.responsivestories.ui.activities.started.onboarding

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.util.media.AudioManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    @Inject
    lateinit var audioManager: AudioManager


    private var shouldPlay = false


    private val txtTopAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.splash_top_animation
        )
    }
    private val txtBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.splash_bottom_animation
        )
    }

    private val cowRightTranslateAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.splash_right_translate
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        // It is deprecated in the API level 30. I will update you with the alternate solution soon.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

//        this.audioManager.getAudioService()?.playMusic()


        // Adding the handler to after the a task after some delay.
        // It is deprecated in the API level 30.
        Handler().postDelayed(
            {

                // If the user is logged in once and did not logged out manually from the app.
                // So, next time when the user is coming into the app user will be redirected to MainScreen.
                // If user is not logged in or logout manually then user will  be redirected to the Login screen as usual.

                // Get the current logged in user id
//                shouldPlay = true
                if(onBoardingFinished()) {
                    // Launch dashboard screen.
                    startActivity(Intent(this@SplashActivity, ViewPagerActivity::class.java))
                } else {
                    // Launch the Login Activity
                    startActivity(Intent(this@SplashActivity, StartActivity::class.java))

                }
                finish() // Call this when your activity is done and should be closed.
            },
            3000
        ) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.

        splash_txt.startAnimation(txtBottomAnimation)
//        splash_cow_frame.startAnimation(txtTopAnimation)
        splash_cow_frame.animate().apply {
            splash_cow_frame.startAnimation(txtTopAnimation)
        }.withEndAction {
            Log.d("txtTopAnimation", "txtTopAnimation: end")
            cow.visibility = View.VISIBLE
            cowRightTranslateAnimation.startOffset = 1000
            cow.startAnimation(cowRightTranslateAnimation)

        }.start()

//        splash_cow_frame.startAnimation(txtTopAnimation.setAnimationListener(object : Animation.AnimationListener))




//        if(txtTopAnimation.hasEnded())
//        {
//            Log.d("txtTopAnimation", "txtTopAnimation: end")
//            cow.visibility = View.VISIBLE
//            cow.startAnimation(cowRightTranslateAnimation)
//
//        }

    }


    private fun onBoardingFinished(): Boolean{
        val sharedPref = this@SplashActivity.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
//
//    override fun onStop() {
//        super.onStop()
//        if (!shouldPlay) {
//            this.audioManager.getAudioService()?.pauseMusic()
//
//        }
//    }
//
//    override fun onResume() {
//        this.audioManager.getAudioService()?.resumeMusic()
//
//
//        super.onResume()
//    }
}