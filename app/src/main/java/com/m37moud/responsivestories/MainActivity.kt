package com.m37moud.responsivestories

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.m37moud.responsivestories.ui.activities.learn.LearnActivity
import com.m37moud.responsivestories.ui.activities.started.onboarding.StartActivity
import com.m37moud.responsivestories.ui.activities.story.StoryActivity
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.activateSetting
import com.m37moud.responsivestories.util.Constants.Companion.showLoading
import com.m37moud.responsivestories.util.FirebaseService
import com.m37moud.responsivestories.util.media.AudioManager
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import com.skydoves.elasticviews.ElasticAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_settings_app.view.*
import javax.inject.Inject

const val TOPIC = "/topics/myTopic2"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var animInvoked: Int = 0
    private var isAnimFinish = false
    private var isResumeAnim = false
    private var shouldPlay = false
    private var shouldAllowBack = false


    private var isStory = false
    private var isLearn = false
    private var isFinish = false


    @Inject
    lateinit var audioManager: AudioManager


    private val grassAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.splash_bottom_animation
        )
    }

    private val exitGrassAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.exit_to_top
        )
    }

    private val buttonsAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.splash_top_animation
        )
    }

    private val exitButtonsAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom
        )
    }


    private val learnLinearLayoutAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.zoom_in
        )
    }

    private val learnLinearLayoutAnimZoomOut: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.zoom_out
        )
    }

    private val learnTxtAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_right_anim
        )
    }
    private val learnImgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_right_anim
        )
    }


    private val storyLinearLayoutAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.zoom_in
        )
    }

    private val storyTxtAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_right_anim
        )
    }
    private val storyImgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_right_anim
        )
    }

    private val txtAndImgInfiniteAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.main_infinit_anim
        )
    }


    //fab button menu
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }
    private var clicked = false

    private lateinit var videosViewModel: VideosViewModel

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_main)


        //play background music
//        shouldPlay = true
//        this.audioManager.getAudioService()?.playMusic()


//        if (!shouldPlay) {
//            shouldPlay = true
//            Constants.startService(this)
//        }

        Log.d("MainActivity", "onCreate: called $shouldPlay")


        main_loading.visibility = View.VISIBLE
        main_parent_frame.visibility = View.INVISIBLE

        animInvoked = 0

        Handler().postDelayed(
            {
                main_loading.visibility = View.GONE
                main_parent_frame.visibility = View.VISIBLE


                //set animation
                initMainActivityAnimation(
                    learn_main_linearLayout,
                    learn_main_txt,
                    learn_main_img,
                    learnLinearLayoutAnim,
                    learnTxtAnim,
                    learnImgAnim,
                    500
                )
                initMainActivityAnimation(
                    story_main_linearLayout,
                    story_main_txt,
                    story_main_img,
                    storyLinearLayoutAnim,
                    storyTxtAnim,
                    storyImgAnim,
                    700
                )
            }, 2500
        )
        supportActionBar?.hide()
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics


        //fab menu
        open_menu_fab.setOnClickListener { onAddButtonClicked() }

        youtube_fab.setOnClickListener { getOpenYoutubeIntent() }

        facebook_fab.setOnClickListener { getOpenFacebookIntent() }
        gmail_fab.setOnClickListener { getOpenMailIntent() }


//        learn_main_img.setOnTouchListener(Constants.Listeners.onTouch)
//        story_main_img.setOnTouchListener(Constants.Listeners.onTouch)
//        img_main_home.setOnTouchListener(Constants.Listeners.onTouch)
//        img_main_setting.setOnTouchListener(Constants.Listeners.onTouch)


        //start story activity
        story_main_img.setOnClickListener {
            Constants.clickSound(this)
            story_main_img.isClickable = false

            // implements animation uising ElasticAnimation
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    if (shouldAllowBack)
                        exitMainActivityAnimation(isStory = true, isLearn = false, isFinish = false)
                }.doAction()


        }

        learn_main_img.setOnClickListener {
            Constants.clickSound(this)
            learn_main_img.isClickable = false

            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    if (shouldAllowBack)
                        exitMainActivityAnimation(isStory = false, isLearn = true, isFinish = false)
                }.doAction()

        }


        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get new FCM registration token
                val token = task.result
                FirebaseService.token = token
                Log.d(TAG, "token: ${token.toString()}")
                val referenceVideos = FirebaseDatabase.getInstance().getReference("RG_token")
                referenceVideos.child("client").setValue(token)
                    .addOnSuccessListener {
                        Log.d("Fetching", "sendRegistrationToServer :  successful ")
                    }
                    .addOnFailureListener { e ->
                        Log.d(
                            "Fetching",
                            " sendRegistrationToServer :  err " + e.message.toString()
                        )

                        //failed to add info to database
                    }
                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            } else {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            }


        }



        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                Log.d("subscribet", "succ ")
                if (!task.isSuccessful) {
                    Log.d("subscribe", "faild ")
                }

                Toast.makeText(baseContext, "subscribeToTopic is Successful", Toast.LENGTH_SHORT)
                    .show()
            }
        MobileAds.initialize(this)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("D6785690C53C6434F5A0BBAA4D808BA6"))
                .build()
        )

//

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, "id")
            param(FirebaseAnalytics.Param.ITEM_NAME, "name")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        }

        videosViewModel = ViewModelProvider(this@MainActivity).get(VideosViewModel::class.java)


        //init background
        main_scroll.visibility = View.VISIBLE

    }

    private fun onAddButtonClicked() {
        fabButtonSound(clicked)
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            facebook_fab.startAnimation(fromBottom)
            gmail_fab.startAnimation(fromBottom)
            youtube_fab.startAnimation(fromBottom)
            open_menu_fab.startAnimation(rotateOpen)
        } else {
            facebook_fab.startAnimation(toBottom)
            gmail_fab.startAnimation(toBottom)
            youtube_fab.startAnimation(toBottom)
            open_menu_fab.startAnimation(rotateClose)
        }
    }

    private fun fabButtonSound(clicked :Boolean){
        if (!clicked) {
            Constants.fabOpenSound(this)
        }else{
            Constants.fabCloseSound(this)

        }

    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            youtube_fab.visibility = View.VISIBLE
            gmail_fab.visibility = View.VISIBLE
            facebook_fab.visibility = View.VISIBLE
        } else {
            youtube_fab.visibility = View.INVISIBLE
            gmail_fab.visibility = View.INVISIBLE
            facebook_fab.visibility = View.INVISIBLE
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            youtube_fab.isClickable = true
            gmail_fab.isClickable = true
            facebook_fab.isClickable = true
        } else {
            youtube_fab.isClickable = false
            gmail_fab.isClickable = false
            facebook_fab.isClickable = false
        }
    }

    override fun onStart() {
        if (!Constants.activateSetting)
            this.audioManager.getAudioService()?.playMusic()

        videosViewModel.saveDownloadStatus(false)
        Log.d("MainActivity", "onStart: called $shouldPlay")

        super.onStart()
    }

    override fun onDestroy() {
//        when app end download status = false
        Log.d("mainAcc", "onDestroy! -> saveDownloadStatus = false")
        videosViewModel.saveDownloadStatus(false)
        super.onDestroy()
    }

    fun settingMainButton(view: View) {
        Constants.clickSound(this)
        ElasticAnimation(view).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
            .setOnFinishListener {
                showSettingDialog()

            }.doAction()

    }

    fun homeMainButton(view: View) {
        Constants.clickSound(this)
        ElasticAnimation(view).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
            .setOnFinishListener {
                shouldPlay = true

                startActivity(
                    Intent(
                        this@MainActivity,
                        StartActivity::class.java
                    )
                )
                finish()
            }.doAction()


    }

    private fun getOpenFacebookIntent(): Intent? = try {
//        context.getPackageManager().getPackageInfo("com.facebook.katana", 0)
        Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/536320790653318")).apply {
            setPackage("com.facebook.katana")
        }.also { readyIntent ->
            startActivity(readyIntent)
        }
    } catch (e: Exception) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.facebook.com/536320790653318")
        )
    }

    private fun getOpenYoutubeIntent(): Intent? = try {


        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ?sub_confirmation=1")
        ).apply {
            setPackage("com.google.android.youtube")
        }.also { readyIntent ->
            startActivity(readyIntent)
        }
    } catch (e: Exception) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ")
        )
    }

    private fun getOpenMailIntent(): Intent? = try {

        Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
//            type = "message/rfc822"
            data = Uri.parse("mailto:m37moud00@gmail.com")
            putExtra(Intent.EXTRA_TEXT, "that is a great app ")
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name).plus(" App "))
        }.also { readyIntent ->
            startActivity(Intent.createChooser(readyIntent, "Send feedback"))
        }

    } catch (e: Exception) {
        Log.e("TAG", "getOpenGMailIntent: ", e)
        Intent(
            Intent.ACTION_SEND,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ?sub_confirmation=1")
        )
    }

    override fun onResume() {
        Log.d(TAG, "onResume: called")
        animInvoked = 0

        if (!Constants.activateSetting)
            this.audioManager.getAudioService()?.resumeMusic()

//        Constants.startService(this)
//        shouldPlay = false

        if (isResumeAnim) {

            learn_main_img.isClickable = true
            story_main_img.isClickable = true
            main_loading.visibility = View.VISIBLE
            main_parent_frame.visibility = View.INVISIBLE
//
            Handler().postDelayed(
                {
                    main_loading.visibility = View.GONE
                    main_parent_frame.visibility = View.VISIBLE

                    initMainActivityAnimation(
                        learn_main_linearLayout,
                        learn_main_txt,
                        learn_main_img,
                        learnLinearLayoutAnim,
                        learnTxtAnim,
                        learnImgAnim,
                        500
                    )

                    initMainActivityAnimation(
                        story_main_linearLayout,
                        story_main_txt,
                        story_main_img,
                        storyLinearLayoutAnim,
                        storyTxtAnim,
                        storyImgAnim,
                        700
                    )

                }, 2500
            )

        }
        super.onResume()

    }


    override fun onBackPressed() {
        showLoading = true
//        this.shouldPlay = true

        if (shouldAllowBack)
            exitMainActivityAnimation(isStory = false, isLearn = false, isFinish = true)

    }

    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onPause() {
//        Log.d(TAG, "onPause: called")
        Log.d("MainActivity", "onPause: called $shouldPlay")

        isResumeAnim = false
//        shouldPlay = false
//        initViewToHide()
        super.onPause()
    }

    private fun initMainActivityAnimation(
        layout: LinearLayout,
        textView: TextView,
        imageView: ImageView,
        layoutAnim: Animation,
        txtAnim: Animation,
        imgAnim: Animation,
        delay: Long
    ) {
        shouldAllowBack = false
        learn_main_img.isClickable = false
        story_main_img.isClickable = false
        img_main_setting.isClickable = false
        img_main_home.isClickable = false
        open_menu_fab.isClickable = false
        main_scroll.start()


//        val translateAnimation =
//            ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, 800f)
//        translateAnimation.repeatCount = 0
//        translateAnimation.startDelay = delay
//        translateAnimation.repeatMode = ValueAnimator.REVERSE
//////        translateAnimation.start()
////
////
//
//        val pvhX: PropertyValuesHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, 2f)
//        val pvhY: PropertyValuesHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2f)
//        val scaleAnimation: ObjectAnimator =
//            ObjectAnimator.ofPropertyValuesHolder(layout, pvhX, pvhY)
////        scaleAnimation.repeatCount = 1
////        translateAnimation.startDelay = 300
//
//        scaleAnimation.repeatMode = ValueAnimator.REVERSE
//
//
//        val setAnimation = AnimatorSet()
//        setAnimation.play(scaleAnimation).after(translateAnimation)
//        setAnimation.start()

        if (animInvoked == 0) {
            open_menu_fab.apply {


                visibility = View.VISIBLE
                alpha = 0f
                scaleX = 0f
                scaleY = 0f

                animate().apply {

                    startDelay = 1000
                    alphaBy(1f)
                    scaleXBy(1f)
                    scaleYBy(1f)

                }.withEndAction {


//                    Toast.makeText(this@MainActivity, "start", Toast.LENGTH_SHORT).show()

                    img_main_home.apply {

                        visibility = View.VISIBLE
                        img_main_setting.visibility = View.VISIBLE

                        animate().apply {
                            startDelay = 300
                            buttonsAnim.duration = 300
                            startAnimation(buttonsAnim)
                            img_main_setting.startAnimation(buttonsAnim)

                        }.withEndAction {
                            main_grass.apply {
                                visibility = View.VISIBLE
                                animate().apply {
                                    startDelay = 150
//                            grassAnim.startOffset = 300
                                    grassAnim.duration = 300
                                    startAnimation(grassAnim)
                                    //we add variable to sure not invoke this method again
                                    animInvoked++
                                }
                            }
                        }.start()
                    }

                }.start()
            }
        }

        layout.apply {
            visibility = View.VISIBLE
            animate().apply {
                startDelay = 1200
                layoutAnim.duration = 300
//                Toast.makeText(this@MainActivity, "layoutAnim", Toast.LENGTH_SHORT).show()
                layoutAnim.startOffset = delay
                layout.startAnimation(layoutAnim)

//                Constants.buttonAppearSound(this@MainActivity)


            }.withEndAction {

                imageView.animate().apply {
                    imageView.visibility = View.VISIBLE
                    startDelay = 300
//                    Toast.makeText(this@MainActivity, "imgAnim", Toast.LENGTH_SHORT).show()

                    imgAnim.startOffset = delay - 200
                    imgAnim.duration = 300
                    imageView.startAnimation(imgAnim)


                }.withEndAction {

                    textView.animate().apply {
                        textView.visibility = View.VISIBLE

                        startDelay = 200

//                        Toast.makeText(this@MainActivity, "txtAnim", Toast.LENGTH_SHORT).show()

                        txtAnim.duration = 300
                        imgAnim.startOffset = delay - 200
                        textView.startAnimation(txtAnim)

                    }.withEndAction {


//                        Toast.makeText(
//                            this@MainActivity,
//                            "txtAnim for infinity ",
//                            Toast.LENGTH_SHORT
//                        ).show()

                        txtAndImgInfiniteAnim.startOffset = delay + 3000
                        imageView.startAnimation(txtAndImgInfiniteAnim)
                        textView.startAnimation(txtAndImgInfiniteAnim)

                        shouldAllowBack = true
                        learn_main_img.isClickable = true
                        story_main_img.isClickable = true
                        img_main_setting.isClickable = true
                        img_main_home.isClickable = true
                        open_menu_fab.isClickable = true

                    }.start()
                }.start()
            }.start()
        }


    }


    private fun exitMainActivityAnimation(
        isStory: Boolean,
        isLearn: Boolean,
        isFinish: Boolean
    ) {
        shouldAllowBack = false
        learn_main_img.isClickable = false
        story_main_img.isClickable = false
        img_main_setting.isClickable = false
        img_main_home.isClickable = false
        open_menu_fab.isClickable = false


        this.shouldPlay = true

        if (animInvoked == 1) {
            open_menu_fab.animate().apply {
                duration = 300
                alphaBy(0f)
                scaleYBy(0f)
                scaleXBy(0f)

            }
            img_main_home.apply {
                animate().apply {
//                            startDelay = 100
                    exitButtonsAnim.duration = 300
                    startAnimation(exitButtonsAnim)
                    img_main_setting.startAnimation(exitButtonsAnim)

                }.withEndAction {
                    main_grass.apply {
                        animate().apply {
//                                    startDelay = 100
//                            grassAnim.startOffset = 300
                            exitGrassAnim.duration = 300
//                            exitGrassAnim.fillAfter = false
                            startAnimation(exitGrassAnim)
//                            open_menu_fab.startAnimation(exitGrassAnim)
                            animInvoked--
                        }

                    }
                }.start()
            }

        }

        learn_main_linearLayout.animate().apply {
            startDelay = 300
            learnLinearLayoutAnimZoomOut.duration = 300
            learn_main_linearLayout.startAnimation(learnLinearLayoutAnimZoomOut)
            story_main_linearLayout.startAnimation(learnLinearLayoutAnimZoomOut)
            learn_main_img.startAnimation(learnLinearLayoutAnimZoomOut)
            story_main_img.startAnimation(learnLinearLayoutAnimZoomOut)
            learn_main_txt.startAnimation(learnLinearLayoutAnimZoomOut)
            story_main_txt.startAnimation(learnLinearLayoutAnimZoomOut)
        }
            .withEndAction {
                shouldAllowBack = true


                when {
                    isStory -> {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                StoryActivity::class.java
                            )
                        )
                        finish()
                    }

                    isLearn -> {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                LearnActivity::class.java
                            )
                        )
                        finish()
                    }
                    isFinish -> {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                StartActivity::class.java
                            )
                        )
                        finish()
                        super.onBackPressed()
                    }
                }

                isResumeAnim = true
                isAnimFinish = true
                shouldAllowBack = true

                initViewToHide()
                learn_main_img.isClickable = true
                story_main_img.isClickable = true
                img_main_setting.isClickable = true
                img_main_home.isClickable = true
                open_menu_fab.isClickable = true
                Constants.fabCloseSound(this)


            }.start()

    }

    private fun initViewToHide() {
        Log.d(TAG, "initViewToHide: called")
        learn_main_linearLayout.visibility = View.INVISIBLE
        story_main_linearLayout.visibility = View.INVISIBLE
        story_main_img.visibility = View.INVISIBLE
        learn_main_img.visibility = View.INVISIBLE
        img_main_home.visibility = View.INVISIBLE
        img_main_setting.visibility = View.INVISIBLE
        learn_main_txt.visibility = View.INVISIBLE
        story_main_txt.visibility = View.INVISIBLE
        open_menu_fab.visibility = View.INVISIBLE
    }

    override fun onStop() {
//        Log.d(TAG, "onStop: called")
        Log.d("MainActivity", "onPause: called $shouldPlay")

        showLoading = false
        if (!shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()
//            Constants.stopService(this)
        }

        super.onStop()
    }

    private fun showSettingDialog() {
        val builder = AlertDialog.Builder(this)

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_settings_app, null)

        if(activateSetting){

            itemView.play_sound_setting.visibility = View.VISIBLE
            itemView.pause_sound_setting.visibility = View.INVISIBLE
        }else{
            itemView.play_sound_setting.visibility = View.INVISIBLE
            itemView.pause_sound_setting.visibility = View.VISIBLE
        }



//        val popUp = PopupWindow(
//            itemView, LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT, false
//        )
//        popUp.isTouchable = true
//        popUp.isFocusable = true
//        popUp.isOutsideTouchable = true
//        popUp.showAsDropDown(img_main_setting)

        builder.setView(itemView)
        val settingsDialog = builder.create()
        settingsDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = settingsDialog.window
        window?.setGravity(Gravity.CENTER)
        window?.attributes?.windowAnimations = R.style.DalogAnimation

//        settingsDialog.setCancelable(false)
//        settingsDialog.setCanceledOnTouchOutside(false)
        itemView.previous_sound_setting.setOnClickListener {
//            if(itemView.play_sound_setting.isVisible)
                itemView.play_sound_setting.visibility = View.INVISIBLE
            itemView.pause_sound_setting.visibility = View.VISIBLE


            this.audioManager.getAudioService()?.previousMusic()

        }

        itemView.play_sound_setting.setOnClickListener {
            Constants.activateSetting = false

            this.audioManager.getAudioService()?.playMusic()
            itemView.play_sound_setting.visibility = View.INVISIBLE
            itemView.pause_sound_setting.visibility = View.VISIBLE

        }

        itemView.pause_sound_setting.setOnClickListener {
            Constants.activateSetting = true

            this.audioManager.getAudioService()?.pauseMusic()
            itemView.play_sound_setting.visibility = View.VISIBLE
            itemView.pause_sound_setting.visibility = View.INVISIBLE

        }

        itemView.next_sound_setting.setOnClickListener {

            itemView.play_sound_setting.visibility = View.INVISIBLE
            itemView.pause_sound_setting.visibility = View.VISIBLE
            this.audioManager.getAudioService()?.nextMusic()

        }
        settingsDialog.show()

    }


}

