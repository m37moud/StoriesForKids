package com.m37moud.responsivestories.ui.activities.started

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.firebase.RemoteConfigUtils
import com.m37moud.responsivestories.firebase.RemoteConfigUtils.getOpenLink
import com.m37moud.responsivestories.ui.activities.learn.LearnActivity
import com.m37moud.responsivestories.ui.activities.story.StoryActivity
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.activateSetting
import com.m37moud.responsivestories.util.Constants.Companion.showLoading
import com.m37moud.responsivestories.util.Logger
import com.m37moud.responsivestories.util.NetworkListener
import com.m37moud.responsivestories.util.media.AudioManager
import com.skydoves.elasticviews.ElasticAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_settings_app.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private const val TAG = "MainActivity"
const val AD_REWARDEDAD_ID = "ca-app-pub-3940256099942544/5224354917"


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var animInvoked: Int = 0
    private var isAnimFinish = false
    private var isResumeAnim = false
    private var repeatLoadAD = false
    private var shouldPlay = false
    private var shouldAllowBack = false

    //ad video reward
    private var mRewardedAd: RewardedAd? = null
    private var mAdIsLoading: Boolean = false
    private var donateLink: String? = null

    //
    private lateinit var networkListener: NetworkListener


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setFullScreen()

        setContentView(R.layout.activity_main)


        // hide views to prepare animation
        initViewToHide()
        Log.d("MainActivity", "onCreate: called $shouldPlay")


        main_loading.visibility = View.VISIBLE
        main_parent_frame.visibility = View.GONE





        animInvoked = 0

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(this@MainActivity)
                .collect { status ->
                    Logger.d(TAG, "NetworkListener is $status")
//                    videosViewModel.networkStatus = status
//                    videosViewModel.showNetworkStatus()
                    if (status) {//if there is connection fetch donate link from firebase


                        lifecycleScope.launch(Dispatchers.IO) {


                            val result: Deferred<String> = async {
                                RemoteConfigUtils.getDonateLink()
//                                Logger.d("donateLink", donateLink)
                            }
                            donateLink = result.await()

                        }
                        if (!donateLink.isNullOrEmpty()) {
                            loadAd()
                        }


                    } else {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                activityIntro()

                            }, 2500
                        )
                    }
                }
        }


//        supportActionBar?.hide()

//        Handler(Looper.getMainLooper()).postDelayed(
//            {
//                activityIntro()
//
//            }, 2500
//        )


        //fab menu
        open_menu_fab.setOnClickListener { onAddButtonClicked() }

        youtube_fab.setOnClickListener { getOpenYoutubeIntent() }

        facebook_fab.setOnClickListener { getOpenFacebookIntent() }
        gmail_fab.setOnClickListener { getOpenMailIntent() }


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
        //start learn activity
        learn_main_img.setOnClickListener {
            Constants.clickSound(this)
            learn_main_img.isClickable = false

            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    if (shouldAllowBack)
                        exitMainActivityAnimation(isStory = false, isLearn = true, isFinish = false)
                }.doAction()

        }


        //init background
        main_scroll.visibility = View.VISIBLE

    }


    override fun onStart() {
        if (!activateSetting)
            this.audioManager.getAudioService()?.playMusic()





        Log.d("MainActivity", "onStart: called $shouldPlay")

        super.onStart()
    }


    override fun onResume() {
        Log.d(TAG, "onResume: called")
        animInvoked = 0

        if (!activateSetting)
            this.audioManager.getAudioService()?.resumeMusic()

        if (isResumeAnim) {

            learn_main_img.isClickable = true
            story_main_img.isClickable = true
            main_loading.visibility = View.VISIBLE
            main_parent_frame.visibility = View.INVISIBLE
//
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    activityIntro()

                }, 2500
            )

        }

        super.onResume()

    }

    private fun activityIntro() {
        main_loading.visibility = View.GONE
        main_parent_frame.visibility = View.VISIBLE

        initMainActivityAnimation(
            learn_main_backgroundImg,
            learn_main_txt,
            learn_main_img,
            learnLinearLayoutAnim,
            learnTxtAnim,
            learnImgAnim,
            500
        )

        initMainActivityAnimation(
            story_main_backgroundImg,
            story_main_txt,
            story_main_img,
            storyLinearLayoutAnim,
            storyTxtAnim,
            storyImgAnim,
            700
        )

    }


    override fun onPause() {
//        Log.d(TAG, "onPause: called")
        Log.d("MainActivity", "onPause: called $shouldPlay")

        isResumeAnim = false
        if (!shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()
//            Constants.stopService(this)
        }
//        shouldPlay = false
//        initViewToHide()
        super.onPause()
    }

    override fun onStop() {
//        Log.d(TAG, "onStop: called")
        Log.d("MainActivity", "onPause: called $shouldPlay")

        showLoading = false


        super.onStop()
    }

    override fun onBackPressed() {
        showLoading = true
//        this.shouldPlay = true

        if (shouldAllowBack)
            exitMainActivityAnimation(isStory = false, isLearn = false, isFinish = true)

    }

    override fun onDestroy() {
//        when app end download status = false
        Log.d("mainAcc", "onDestroy! -> saveDownloadStatus = false")
        mRewardedAd = null
        super.onDestroy()
    }


    fun settingMainButton(view: View) {
        img_main_setting.isClickable = false
        Constants.clickSound(this)
        ElasticAnimation(view).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
            .setOnFinishListener {
                showSettingDialog()


            }.doAction()

    }

    fun homeMainButton(view: View) {
        img_main_home.isClickable = false

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
//
//    private fun setFullScreen() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//    }


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

    private fun fabButtonSound(clicked: Boolean) {
        if (!clicked) {
            Constants.fabOpenSound(this)
        } else {
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

    private fun initMainActivityAnimation(
        backgroundImg: ImageView,
        textView: TextView,
        imageView: ImageView,
        layoutAnim: Animation,
        txtAnim: Animation,
        imgAnim: Animation,
        delay: Long
    ) {
        startInitButtons()


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

        backgroundImg.apply {
            visibility = View.VISIBLE
            animate().apply {
                startDelay = 1200
                layoutAnim.duration = 300
//                Toast.makeText(this@MainActivity, "layoutAnim", Toast.LENGTH_SHORT).show()
                layoutAnim.startOffset = delay
                backgroundImg.startAnimation(layoutAnim)

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

                        finishInitButtons()

//                        donateLink = RemoteConfigUtils.getDonateLink()


                    }.start()
                }.start()
            }.start()
        }


    }


    private fun startInitButtons() {
        shouldAllowBack = false
        learn_main_img.isClickable = false
        story_main_img.isClickable = false
        img_main_setting.isClickable = false
        img_main_home.isClickable = false
        open_menu_fab.isClickable = false
        main_scroll.start()
    }

    private fun finishInitButtons() {
        shouldAllowBack = true
        learn_main_img.isClickable = true
        story_main_img.isClickable = true
        img_main_setting.isClickable = true
        img_main_home.isClickable = true
        open_menu_fab.isClickable = true
    }

    private fun exitMainActivityAnimation(
        isStory: Boolean,
        isLearn: Boolean,
        isFinish: Boolean
    ) {
        startInitButtons()

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
//                        startActivity(
//                            Intent(
//                                this@MainActivity,
//                                StartActivity::class.java
//                            )
//                        )
//                        finish()
                        super.onBackPressed()
                    }
                }

                isResumeAnim = true
                isAnimFinish = true

                initViewToHide()

                finishInitButtons()

                Constants.fabCloseSound(this)

            }.start()

    }

    private fun initViewToHide() {
        Log.d(TAG, "initViewToHide: called")
        learn_main_backgroundImg.visibility = View.INVISIBLE
        story_main_backgroundImg.visibility = View.INVISIBLE
        story_main_img.visibility = View.INVISIBLE
        learn_main_img.visibility = View.INVISIBLE
        img_main_home.visibility = View.INVISIBLE
        img_main_setting.visibility = View.INVISIBLE
        learn_main_txt.visibility = View.INVISIBLE
        story_main_txt.visibility = View.INVISIBLE
        open_menu_fab.visibility = View.INVISIBLE
        main_grass.visibility = View.INVISIBLE
    }


    private fun showSettingDialog() {

        val builder = AlertDialog.Builder(this)

        val itemView: View = LayoutInflater.from(this).inflate(R.layout.layout_settings_app, null)

        if (activateSetting) {

            itemView.play_sound_setting.visibility = View.VISIBLE
            itemView.pause_sound_setting.visibility = View.INVISIBLE
        } else {
            itemView.play_sound_setting.visibility = View.INVISIBLE
            itemView.pause_sound_setting.visibility = View.VISIBLE
        }

        Logger.d("donateLink", donateLink)
        if (donateLink.isNullOrBlank()) {
            val txt = this.getString(R.string.donate_by_watch_vid)
            itemView.donate_txt.text = txt
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
        window?.attributes?.windowAnimations = R.style.DialogAnimation

//        settingsDialog.setCancelable(false)
//        settingsDialog.setCanceledOnTouchOutside(false)
        itemView.previous_sound_setting.setOnClickListener {
            Constants.clickSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {

//            if(itemView.play_sound_setting.isVisible)
                    itemView.play_sound_setting.visibility = View.INVISIBLE
                    itemView.pause_sound_setting.visibility = View.VISIBLE


                    this.audioManager.getAudioService()?.previousMusic()
                }.doAction()


        }

        itemView.play_sound_setting.setOnClickListener {
            Constants.clickSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {

                    activateSetting = false

                    this.audioManager.getAudioService()?.playMusic()
                    itemView.play_sound_setting.visibility = View.INVISIBLE
                    itemView.pause_sound_setting.visibility = View.VISIBLE
                }.doAction()


        }

        itemView.pause_sound_setting.setOnClickListener {
            Constants.clickSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {

                    activateSetting = true

                    this.audioManager.getAudioService()?.pauseMusic()
                    itemView.play_sound_setting.visibility = View.VISIBLE
                    itemView.pause_sound_setting.visibility = View.INVISIBLE
                }.doAction()


        }

        itemView.next_sound_setting.setOnClickListener {
            Constants.clickSound(this)

            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    itemView.play_sound_setting.visibility = View.INVISIBLE
                    itemView.pause_sound_setting.visibility = View.VISIBLE
                    this.audioManager.getAudioService()?.nextMusic()
                }.doAction()


        }
        itemView.share_container.setOnClickListener {
            Constants.clickSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {


                    share()
                }.doAction()

        }

        itemView.donate_container.setOnClickListener {
            Constants.clickSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
//
//                    String url = ¨https://paul.kinlan.me/¨;
//                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                    CustomTabsIntent customTabsIntent = builder.build();
//                    customTabsIntent.launchUrl(this, Uri.parse(url));

//
                    if (donateLink.isNullOrBlank()) {
                        showRewardAd()
                        Log.d("loadAd", "mRewardedAd = $mRewardedAd")

                    } else {
                        //https://www.patreon.com/m37moud
                        val intent = Intent(this, WebViewActivity::class.java)
                        intent.putExtra("donateLink", donateLink)

                        startActivity(intent)
                    }


                }.doAction()

        }

        settingsDialog.show()
        settingsDialog.setOnDismissListener {
            Constants.fabCloseSound(this)
            img_main_setting.isClickable = true
            mRewardedAd = null
//            mAdIsLoading = false

        }


    }

    private fun share() {
        val applicationNameId: Int = this.applicationInfo.labelRes
        val appPackageName: String = this.packageName
        val appName = this.getString(R.string.app_name)
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val text = this.getString(R.string.share_app)
        val link = getOpenLink().plus(appPackageName) // get store link
        Logger.d(text.toString())
        i.putExtra(Intent.EXTRA_SUBJECT, appName)
        i.putExtra(Intent.EXTRA_TEXT, "\n$appName \n\n$text \n\n $link \n\n")
        startActivity(Intent.createChooser(i, text))
//

//
//        ShareCompat.IntentBuilder.from(this)
//            .setType("text/plain")
//            .setChooserTitle("Chooser title")
//            .setText("http://play.google.com/store/apps/details?id=$appPackageName")
//            .startChooser();
    }

    private fun loadAd() {
        MobileAds.initialize(this@MainActivity)


        val mRewardID = if (TextUtils.isEmpty(Constants.addRewardAds))
            AD_REWARDEDAD_ID
        else
            Constants.addRewardAds.toString()

        Logger.d("load", Constants.addRewardAds.toString())

        try {
            val adRequest = AdRequest.Builder().build()


            RewardedAd.load(
                this, mRewardID, adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Logger.d("loadAd", adError.message)
                        mRewardedAd = null
                        mAdIsLoading = false
                        val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                                "message: ${adError.message}"
                        Toast.makeText(
                            this@MainActivity,
                            "FailedToLoad  error  = $error",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (!repeatLoadAD) {
                            activityIntro()
                            repeatLoadAD = true // it decide will show loading animation again

                        } //start activity intro

                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {

                        Logger.d("loadAd", "Ad was loaded.")
                        mRewardedAd = rewardedAd
                        mAdIsLoading = false

                        if (!repeatLoadAD) {
                            activityIntro()
                            repeatLoadAD = true // it decide will show loading animation again

                        } //start activity intro

                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.d("showAds", " : catch " + e)
        }


    }

    private fun showRewardAd() {
        if (mRewardedAd != null) {
//            shouldPlay = false
            mRewardedAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("loadAd", "showInterstitial Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
//                        mRewardedAd = null
                        mAdIsLoading = false
                        loadAd()

//                                shouldPlay = true
//                                loadAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        Log.d("loadAd", "showInterstitial Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
//                        mRewardedAd = null
//                        shouldPlay = true

                    }

                    override fun onAdShowedFullScreenContent() {

                        Log.d("loadAd", "showInterstitial Ad showed fullscreen content.")
                        // Called when ad is dismissed.
//                        mRewardedAd = null
                        mAdIsLoading = true

                    }
                }
//            mAdIsLoading = true
            mRewardedAd?.show(this, OnUserEarnedRewardListener() {

                fun onUserEarnedReward(rewardItem: RewardItem) {
//                    var rewardAmount = rewardItem.getReward()
                    var rewardType = rewardItem.type
                    Log.d("loadAd", "User earned the reward.")
                }
            })

        } else {
//            shouldPlay = true
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
        }
    }


}

