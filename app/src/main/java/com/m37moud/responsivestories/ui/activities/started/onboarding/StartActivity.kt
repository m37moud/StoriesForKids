package com.m37moud.responsivestories.ui.activities.started.onboarding

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.responsivestories.MainActivity
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.activateSetting
import com.m37moud.responsivestories.util.Constants.Companion.buttonAppearSound
import com.m37moud.responsivestories.util.Constants.Companion.clickSound
import com.m37moud.responsivestories.util.Constants.Companion.fabCloseSound
import com.m37moud.responsivestories.util.Constants.Companion.showLoading
import com.m37moud.responsivestories.util.media.AudioManager
import com.skydoves.elasticviews.ElasticAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.layout_exit_app.view.*
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {


    @Inject
    lateinit var audioManager: AudioManager
    private var shouldPlay = false
    private var shouldAllowBack = false

//    private val audioManager: AudioManager by lazy {
//        EntryPointAccessors.fromApplication (applicationContext,
//            PodcastEntryPoint::class.java).audioManager()
//    }

    //bird animation
    private val birdAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.start_bird_anim
        )
    }

    //play button
    private val playAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.zoom_in
        )
    }

    private var backPressed = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_start)


//


        //play background music
//        this.audioManager.getAudioService()?.playMusic()


//        shouldPlay = true
//        if (!shouldPlay) {
//            Constants.startService(this)
//        }
        Log.d("StartActivity", "onCreate: $shouldPlay ")

//        videosViewModel.readBackOnline.observe(this@StoryActivity, Observer {
//            videosViewModel.backOnline = it
//        })
//        start.animate().duration = 200


//        start.setOnTouchListener(Constants.Listeners.onTouch)

        start.setOnClickListener {
            clickSound(this)
            // implements animation uising ElasticAnimation
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    start.isClickable = false
                    this.shouldPlay = true
                    val intent = Intent(this@StartActivity, MainActivity::class.java)

//           val pair : android.util.Pair =  Pair<View,String>(start , "toNextButton")
                    val activityOption = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        android.util.Pair.create(start, "toNextButton")
                    )

//            start_loading.visibility = View.VISIBLE
//            start_parent_frame.visibility = View.GONE
                    showLoading = true
                    startActivity(intent)
                    finish()
                }.doAction()


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

//        val androidColors = resources.getIntArray(R.array.androidcolors)
//        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
//        view.setBackgroundColor(randomAndroidColor)


//        initBackgroundColor(start_FrameLayout,this@StartActivity)
        start_frameLayout_scroll.visibility = View.VISIBLE
        start_scroll.visibility = View.VISIBLE

        start_bird.startAnimation(birdAnim)
        playAnim.startOffset = 400
        start.animate().apply {
            startDelay = 300
            start.startAnimation(playAnim)
            if (shouldAllowBack)
                buttonAppearSound(this@StartActivity)

        }

    }

    override fun onResume() {
//      Constants.startService(this)
//        shouldPlay = false
        if (!activateSetting)
            this.audioManager.getAudioService()?.resumeMusic()


        Log.d("StartActivity", "onResume: $shouldPlay ")

        if (showLoading) {
            main_loading.visibility = View.VISIBLE
            start_parent_frame.visibility = View.INVISIBLE

            Handler().postDelayed(
                {
                    shouldAllowBack = true
                    main_loading.visibility = View.INVISIBLE
                    start_parent_frame.visibility = View.VISIBLE
                    start_scroll.start()

                    showLoading = false

                }, 2500
            )

        }
        super.onResume()
        Log.d("onResume", "onResume: $showLoading")
        start.isClickable = true

//        start_loading.visibility = View.VISIBLE
//        start_parent_frame.visibility = View.INVISIBLE


    }


//    override fun onPause() {
//        super.onPause()
//
//        Log.d("StartActivity", "onPause: $shouldPlay ")
//        Log.d("StartActivity", "onPause: $showLoading")
//
////        if (!this.shouldPlay) {
////            this.audioManager.getAudioService()?.pauseMusic()
////
////        }
//
//    }

    override fun onStop() {
//        showLoading = false
//        shouldPlay = false
        Log.d("StartActivity", "onStop: $shouldPlay ")

        if (!this.shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()

        }


        super.onStop()
    }


    override fun onStart() {
        start.isClickable = true
//        shouldAllowBack = true

//        shouldPlay = false
//        Log.d("StartActivity", "onStart: $shouldPlay ")
        if (!activateSetting)
            this.audioManager.getAudioService()?.playMusic()



        //init loading then activity
        main_loading.visibility = View.VISIBLE
        start_parent_frame.visibility = View.INVISIBLE
        Handler().postDelayed(
            {
                shouldAllowBack = true
                main_loading.visibility = View.INVISIBLE
                start_parent_frame.visibility = View.VISIBLE
                start_scroll.start()

                showLoading = false

            }, 4500
        )

        super.onStart()
    }

    override fun onBackPressed() {
        if (shouldAllowBack) {
            showExitDialog()
//            if (backPressed + 2000 > System.currentTimeMillis()) {
//
//                showExitDialog()
//
//            } else
////                Toast.makeText(applicationContext, "Press Back again to Exit", Toast.LENGTH_SHORT)
////                    .show()
//
//                backPressed = System.currentTimeMillis()
        }
    }


    private fun showExitDialog() {
        start_scroll.stop()
        fabCloseSound(this)
        shouldPlay = false
        val builder = AlertDialog.Builder(this)

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_exit_app, null)

        builder.setView(itemView)
        val exitDialog = builder.create()
        exitDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = exitDialog.window
        window?.setGravity(Gravity.CENTER)
        window?.attributes?.windowAnimations = R.style.DalogAnimation

        exitDialog.setCancelable(false)
        exitDialog.setCanceledOnTouchOutside(false)
        itemView.exit_app.setOnClickListener {
            Constants.clickSound(this)


            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    shouldPlay = false

                    exitDialog.dismiss()
                    finishAfterTransition()

//            super.onBackPressed()
//          onDestroy()
                }.doAction()


        }

        itemView.cancel_exit_app.setOnClickListener {
            Constants.fabCloseSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    shouldPlay = false

                    exitDialog.dismiss()
                    start_scroll.start()

                }.doAction()


        }

        itemView.rate.setOnClickListener {
            Constants.clickSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    shouldPlay = false

                    exitDialog.dismiss()
                    Toast.makeText(this, "Rate APP", Toast.LENGTH_SHORT).show()
                }.doAction()


        }
        exitDialog.show()


    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("StartActivity", "onDestroy: $shouldPlay ")
//

        if (!shouldPlay) {
            this.audioManager.doUnbindService()
            this.audioManager.getAudioService()?.onDestroy()

        }


    }
}


//
//
//// Fade the button out and back in
//ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(alphaButton,
//View.ALPHA, 0);
//alphaAnimation.setRepeatCount(1);
//alphaAnimation.setRepeatMode(ValueAnimator.REVERSE);
//
//// Move the button over to the right and then back
//ObjectAnimator translateAnimation =
//ObjectAnimator.ofFloat(translateButton, View.TRANSLATION_X, 800);
//translateAnimation.setRepeatCount(1);
//translateAnimation.setRepeatMode(ValueAnimator.REVERSE);
//
//// Spin the button around in a full circle
//ObjectAnimator rotateAnimation =
//ObjectAnimator.ofFloat(rotateButton, View.ROTATION, 360);
//rotateAnimation.setRepeatCount(1);
//rotateAnimation.setRepeatMode(ValueAnimator.REVERSE);
//
//// Scale the button in X and Y. Note the use of PropertyValuesHolder to animate
//// multiple properties on the same object in parallel.
//PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 2);
//PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2);
//ObjectAnimator scaleAnimation =
//ObjectAnimator.ofPropertyValuesHolder(scaleButton, pvhX, pvhY);
//scaleAnimation.setRepeatCount(1);
//scaleAnimation.setRepeatMode(ValueAnimator.REVERSE);
//
//// Run the animations above in sequence
//AnimatorSet setAnimation = new AnimatorSet();
//setAnimation.play(translateAnimation).after(alphaAnimation).before(rotateAnimation);
//setAnimation.play(rotateAnimation).before(scaleAnimation);
//
//setupAnimation(alphaButton, alphaAnimation, R.animator.fade);
//setupAnimation(translateButton, translateAnimation, R.animator.move);
//setupAnimation(rotateButton, rotateAnimation, R.animator.spin);
//setupAnimation(scaleButton, scaleAnimation, R.animator.scale);
//setupAnimation(setButton, setAnimation, R.animator.combo);
//
//}
//
//private void setupAnimation(View view, final Animator animation, final int animationID) {
//    view.setOnClickListener(new View.OnClickListener() {
//        public void onClick(View v) {
//            // If the button is checked, load the animation from the given resource
//            // id instead of using the passed-in animation parameter. See the xml files
//            // for the details on those animations.
//            if (mCheckBox.isChecked()) {
//                Animator anim = AnimatorInflater.loadAnimator(PropertyAnimations.this, animationID);
//                anim.setTarget(v);
//                anim.start();
//                return;
//            }
//            animation.start();
//        }
//    });
//}