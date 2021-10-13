package com.m37moud.responsivestories.ui.activities.started.onboarding

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.responsivestories.MainActivity
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.util.Constants.Companion.showLoading
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {

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

//        videosViewModel.readBackOnline.observe(this@StoryActivity, Observer {
//            videosViewModel.backOnline = it
//        })
        start.animate().duration = 200
        start.setOnTouchListener { view , arg1 ->
            if (arg1.action == MotionEvent.ACTION_DOWN) {
                start.animate().setInterpolator(DecelerateInterpolator()).scaleX(.7f).scaleY(.7f)
            } else if (arg1.action == MotionEvent.ACTION_UP) {
                start.animate().setInterpolator(OvershootInterpolator(10f)).scaleX(1f).scaleY(1f)
            }
            false
        }
        start.setOnClickListener {
            start.isClickable = false
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

//            Handler().postDelayed(
//                {
//
//                }, 2500
//            )

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

//        val androidColors = resources.getIntArray(R.array.androidcolors)
//        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
//        view.setBackgroundColor(randomAndroidColor)


//        initBackgroundColor(start_FrameLayout,this@StartActivity)
        start_frameLayout_scroll.visibility = View.VISIBLE
        start_scroll.visibility = View.VISIBLE

        start_bird.startAnimation(birdAnim)
        start.startAnimation(playAnim)

    }

    override fun onResume() {

        if (showLoading) {
            main_loading.visibility = View.VISIBLE
            start_parent_frame.visibility = View.INVISIBLE

            Handler().postDelayed(
                {
                    main_loading.visibility = View.INVISIBLE
                    start_parent_frame.visibility = View.VISIBLE
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


    override fun onPause() {
        Log.d("onResume", "onPause: $showLoading")


//        start_loading.visibility = View.VISIBLE
//        start_parent_frame.visibility = View.INVISIBLE
        super.onPause()
    }

    override fun onStop() {
        showLoading = false

        super.onStop()
    }


    override fun onStart() {
        start.isClickable = true
        main_loading.visibility = View.GONE
        start_parent_frame.visibility = View.VISIBLE
        super.onStart()
    }

    override fun onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()

        } else
            Toast.makeText(applicationContext, "Press Back again to Exit", Toast.LENGTH_SHORT)
                .show()

        backPressed = System.currentTimeMillis()
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