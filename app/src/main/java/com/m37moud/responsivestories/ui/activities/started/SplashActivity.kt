package com.m37moud.responsivestories.ui.activities.started

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
import com.m37moud.responsivestories.ui.activities.started.onboarding.ViewPagerActivity
import com.m37moud.responsivestories.util.media.AudioManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
import javax.inject.Inject

private const val TAG = "SplashActivity"

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    @Inject
    lateinit var audioManager: AudioManager

    // corotine
    val activityScope = CoroutineScope(Dispatchers.Main)


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        setContentView(R.layout.activity_splash)






    }


    private fun onBoardingFinished(): Boolean {
        Log.d(TAG, "onBoardingFinished: called")

        val sharedPref =
            this@SplashActivity.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
//

    override fun onStart() {
        super.onStart()
        this.audioManager.doBindService()

        //start animation
        splash_txt.startAnimation(txtBottomAnimation)

        splash_img_background.startAnimation(txtTopAnimation)
        txtTopAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                bay_face_lottie.apply {
                    visibility = View.VISIBLE
                        playAnimation()

                    // This is used to hide the status bar and make the splash screen as a full screen activity.
                    // It is deprecated in the API level 30. I will update you with the alternate solution soon.

                    activityScope.launch {


                        delay(2000)


                        shouldPlay = true
                        if (!onBoardingFinished()) {
                            Log.d(TAG, "onCreate: ${onBoardingFinished()}")
                            // Launch dashboard screen.
                            startActivity(Intent(this@SplashActivity, ViewPagerActivity::class.java))
                        } else {
                            Log.d(TAG, "onStart: ${onBoardingFinished()}")
                            // Launch the start Activity
                            startActivity(Intent(this@SplashActivity, StartActivity::class.java))

                        }
                        // Call this when your activity is done and should be closed.

                        finish()


                    }


                }

            }
        })

    }


//
//    override fun onResume() {
//        this.audioManager.getAudioService()?.resumeMusic()
//
//
//        super.onResume()
//    }

    override fun onPause() {
        super.onPause()
        activityScope.cancel()
        if (!shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()

        }

    }
    override fun onStop() {
        super.onStop()
        if (!shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()

        }
        splash_txt.clearAnimation()
        splash_cow_frame.clearAnimation()
        bay_face_lottie.pauseAnimation()
    }

}