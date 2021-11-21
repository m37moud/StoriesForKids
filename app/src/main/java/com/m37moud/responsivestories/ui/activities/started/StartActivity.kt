package com.m37moud.responsivestories.ui.activities.started

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.firebase.RemoteConfigUtils
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.activateSetting
import com.m37moud.responsivestories.util.Constants.Companion.addRewardAds
import com.m37moud.responsivestories.util.Constants.Companion.bannerAds
import com.m37moud.responsivestories.util.Constants.Companion.clickSound
import com.m37moud.responsivestories.util.Constants.Companion.fabCloseSound
import com.m37moud.responsivestories.util.Constants.Companion.interstitialAds
import com.m37moud.responsivestories.util.Constants.Companion.showAdsFromRemoteConfig
import com.m37moud.responsivestories.util.Constants.Companion.showLoading
import com.m37moud.responsivestories.util.FirebaseService
import com.m37moud.responsivestories.util.NetworkListener
import com.m37moud.responsivestories.util.NetworkResult
import com.m37moud.responsivestories.util.media.AudioManager
import com.m37moud.responsivestories.viewmodel.MainViewModel
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import com.skydoves.elasticviews.ElasticAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.layout_exit_app.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TOPIC = "/topics/myTopic2"
private const val TAG = "StartActivity"


@AndroidEntryPoint
class StartActivity : AppCompatActivity() {


    @Inject
    lateinit var audioManager: AudioManager
    private var shouldPlay = false
    private var shouldAllowBack = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var videosViewModel: VideosViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var networkListener: NetworkListener



    //bird animation
    private val birdAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.start_bird_anim
        )
    }

    private val flashTxtAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.start_txt_flashing
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        setContentView(R.layout.activity_start)
        videosViewModel = ViewModelProvider(this@StartActivity).get(VideosViewModel::class.java)
        mainViewModel = ViewModelProvider(this@StartActivity).get(MainViewModel::class.java)

        videosViewModel.readBackOnline.observe(this@StartActivity, Observer {
            videosViewModel.backOnline = it
        })
        //check for internet connection
        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(this@StartActivity)
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    videosViewModel.networkStatus = status
                    videosViewModel.showNetworkStatus()
                    //read database
                    videosViewModel.readBottomSheetExitStatus.observe(
                        this@StartActivity,
                        Observer { exitStatus ->
                            Log.d("bottomSheetExit", exitStatus.toString())
                            if (exitStatus) {
                                RemoteConfigUtils.init(this@StartActivity)

                            } else {
                                showSnackBar("No internet Connection Please Connect Internet")
                            }
                        })
                }
        }
        //


        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        Log.d("StartActivity", "onCreate: $shouldPlay ")

        main_loading.visibility = View.VISIBLE
        start_parent_frame.visibility = View.INVISIBLE

        initImgScroll()

        //fetch ads model
        getAdsFolderFromFirebase()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                shouldAllowBack = true
                main_loading.visibility = View.GONE
                start_parent_frame.visibility = View.VISIBLE
                start_frameLayout_scroll.visibility = View.VISIBLE
                start_scroll.visibility = View.VISIBLE

                showLoading = false

                //check to update the app
                checkForUpdate()

            }, 1500
        )





        lifecycleScope.launch(Dispatchers.IO) {
            FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get new FCM registration token
                    val token = task.result
                    FirebaseService.token = token
                    Log.d("StartActivity", "token: ${token.toString()}")
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
                    Log.d("StartActivity", msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("StartActivity", "Fetching FCM registration token failed", task.exception)
                }

            }


            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                .addOnCompleteListener { task ->
                    Log.d("subscribet", "succ ")
                    if (!task.isSuccessful) {
                        Log.d("subscribe", "faild ")
                    }

//                    Toast.makeText(
//                        baseContext,
//                        "subscribeToTopic is Successful",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
                }

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_ID, "id")
                param(FirebaseAnalytics.Param.ITEM_NAME, "name")
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
            }
        }


        start.setOnClickListener {
            clickSound(this)
            // implements animation uising ElasticAnimation
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    start.isClickable = false
                    this.shouldPlay = true
                    val intent = Intent(this@StartActivity, MainActivity::class.java)

//           val pair : android.util.Pair =  Pair<View,String>(start , "toNextButton")
//                    val activityOption = ActivityOptions.makeSceneTransitionAnimation(
//                        this,
//                        android.util.Pair.create(start, "toNextButton")
//                    )

//            start_loading.visibility = View.VISIBLE
//            start_parent_frame.visibility = View.GONE
                    showLoading = true
                    startActivity(intent)
                    overridePendingTransition(0, 0)
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


//        start_bird.startAnimation(birdAnim)
//        start_txt.startAnimation(flashTxtAnim)
//        playAnim.startOffset = 400
//        start.animate().apply {
//            startDelay = 300
//            start.startAnimation(playAnim)
//            if (shouldAllowBack)
//                buttonAppearSound(this@StartActivity)
//
//        }

    }


    override fun onStart() {
        //play all anim
        startAllAnim()

        start.isClickable = true
        //Log.d("StartActivity", "onStart: $shouldPlay ")
        if (!activateSetting)
            this.audioManager.getAudioService()?.playMusic()

        //prepare fo check downloads to story
        videosViewModel.saveDownloadStatus(false)


        super.onStart()
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

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    shouldAllowBack = true
                    main_loading.visibility = View.INVISIBLE
                    start_parent_frame.visibility = View.VISIBLE

                    startAllAnim()

                    showLoading = false

                }, 2000
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
        //stop all animation
        stopAllAnim()

        Log.d("StartActivity", "onStop: $shouldPlay ")

        if (!this.shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()

        }


        super.onStop()
    }


    override fun onBackPressed() {
        if (shouldAllowBack) {
            showExitDialog()
        }
    }


    private fun showExitDialog() {
        //stop all animation
        stopAllAnim()

        fabCloseSound(this)
        shouldPlay = false
        val builder = AlertDialog.Builder(this)

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_exit_app, null)

        builder.setView(itemView)
        val exitDialog = builder.create()
        exitDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = exitDialog.window
        window?.setGravity(Gravity.CENTER)
        window?.attributes?.windowAnimations = R.style.DialogAnimation

        exitDialog.setCancelable(false)
        exitDialog.setCanceledOnTouchOutside(false)
        itemView.exit_app.setOnClickListener {
            Constants.clickSound(this)


            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    shouldPlay = false
                    exitDialog.setOnDismissListener {
                        videosViewModel.saveDownloadStatus(false)

                        finishAffinity()

                    }
                    exitDialog.dismiss()


                }.doAction()


        }

        itemView.cancel_exit_app.setOnClickListener {
            Constants.fabCloseSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    shouldPlay = false

                    exitDialog.dismiss()
                    // resume All animation
                    startAllAnim()

                }.doAction()


        }

        itemView.rate.setOnClickListener {
            Constants.clickSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    shouldPlay = false
//resume animation
                    startAllAnim()

                    exitDialog.dismiss()
                    Toast.makeText(this, "Rate APP", Toast.LENGTH_SHORT).show()
                }.doAction()


        }
        exitDialog.show()


    }

    private fun initImgScroll() {

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels
        start_scroll.layoutParams.height = screenHeight
        start_scroll.layoutParams.width = screenWidth


    }


    private fun getAdsFolderFromFirebase() {

        Log.d("AdsFolderFromFirebase", "getCategories called!")
        mainViewModel.getAdsFolder()

        mainViewModel.adsFolderResponse.observe(this@StartActivity, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    Log.d("AdsFolderFromFirebase", "getCategories sucsess!")
                    response.data?.let {
                        showAdsFromRemoteConfig = it.activateAds as Boolean
                        Log.d(
                            "AdsFolderFromFirebase",
                            "showAdsFromRemoteConfig sucsess! ${it.activateAds}"
                        )

                        addRewardAds = it.addRewardAds?.toString()!!
                        Log.d(
                            "AdsFolderFromFirebase",
                            "showAdsFromRemoteConfig sucsess! ${it.addRewardAds}"
                        )

                        bannerAds = it.bannerAds?.toString()!!
                        Log.d(
                            "AdsFolderFromFirebase",
                            "showAdsFromRemoteConfig sucsess! ${bannerAds}"
                        )
                        interstitialAds = it.interstitialAds.toString()
                    }
                }

                is NetworkResult.Error -> {
                    Log.d(
                        "AdsFolderFromFirebase",
                        "mah AdsFolderFromFirebase error! \n" + response.toString()
                    )

                    Toast.makeText(
                        this@StartActivity,
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {

                    Log.d("AdsFolderFromFirebase", "AdsFolderFromFirebase Loading!")
                }
            }
        })
    }


    //check app to update

    private fun checkForUpdate() {
        videosViewModel.readShouldDownload.observe(this@StartActivity, Observer {
            Log.d(TAG, " called! + it = $it ")
            if (!it) {

                val appVersion: String = getAppVersion(this)
                val remoteConfig = FirebaseRemoteConfig.getInstance()

                val currentVersion =
                    remoteConfig.getString("min_version_of_app")
                val minVersion =
                    remoteConfig.getString("latest_version_of_app")

                Log.d("checkForUpdate", "currentVersion: $currentVersion ")
                Log.d("checkForUpdate", "minVersion: $minVersion ")
                Log.d("checkForUpdate", "appVersion: $appVersion ")
                if (!TextUtils.isEmpty(minVersion) && !TextUtils.isEmpty(appVersion) && checkMandateVersionApplicable(
                        getAppVersionWithoutAlphaNumeric(minVersion),
                        getAppVersionWithoutAlphaNumeric(appVersion)
                    )
                ) {
                    Log.d("checkForUpdate", "appVersion:force ")

                    onUpdateNeeded(true)
                } else if (!TextUtils.isEmpty(currentVersion) && !TextUtils.isEmpty(appVersion) && !TextUtils.equals(
                        currentVersion,
                        appVersion
                    )
                ) {
                    Log.d("checkForUpdate", "appVersion:flex ")

                    onUpdateNeeded(false)
                } else {

                    moveForward()
                }
            }

        })

    }


    private fun checkMandateVersionApplicable(
        minVersion: String,
        appVersion: String
    ): Boolean {
        return try {
            val minVersionInt = minVersion.toInt()
            val appVersionInt = appVersion.toInt()
            minVersionInt > appVersionInt
        } catch (exp: NumberFormatException) {
            false
        }
    }

    private fun getAppVersion(context: Context): String {
        var result: String? = ""
        try {
            result = context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("TAG", e.message.toString())
        }
        return result ?: ""
    }

    private fun getAppVersionWithoutAlphaNumeric(result: String): String {
        var version_str = ""
        version_str = result.replace(".", "")
        return version_str
    }

    private fun onUpdateNeeded(isMandatoryUpdate: Boolean) {
        //stop anim
        stopAllAnim()

        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.update_app))
            .setCancelable(false)
            .setMessage(if (isMandatoryUpdate) getString(R.string.dialog_update_available_message) else "A new version is found on Play store, please update for better usage.")
            .setPositiveButton(getString(R.string.update_now))
            { dialog, which ->

                openAppOnPlayStore(this, null)
                finishAffinity()
            }

        if (!isMandatoryUpdate) {
            dialogBuilder.setNegativeButton(getString(R.string.later)) { dialog, which ->
                moveForward()
                dialog?.dismiss()
                // resume All animation
                startAllAnim()

            }.create()
        }
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
    }

    private fun moveForward() {
        Toast.makeText(this, "Next Page Intent", Toast.LENGTH_SHORT).show()
    }

    private fun openAppOnPlayStore(ctx: Context, package_name: String?) {
        var package_name = package_name
        if (package_name == null) {
            package_name = ctx.packageName
        }
        val uri = Uri.parse("market://details?id=$package_name")
        openURI(ctx, uri, "Play Store not found in your device")
    }

    private fun openURI(
        ctx: Context,
        uri: Uri?,
        error_msg: String?
    ) {
        val i = Intent(Intent.ACTION_VIEW, uri)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        if (ctx.packageManager.queryIntentActivities(i, 0).size > 0) {
            ctx.startActivity(i)
        } else if (error_msg != null) {
            Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show()
        }
    }


// end check methods

    private fun stopAllAnim() {
        start_scroll.stop()
        start_cat.pauseAnimation()
        start_bird.clearAnimation()
        start_txt.clearAnimation()
        birdAnim.cancel()
        birdAnim.reset()
        flashTxtAnim.cancel()
        flashTxtAnim.reset()


    }

    private fun startAllAnim() {
        start_scroll.start()
        start_cat.resumeAnimation()
        start_bird.startAnimation(birdAnim)
        start_txt.startAnimation(flashTxtAnim)

    }
    private fun showSnackBar(msg :String){
        val view = this.findViewById<View>(R.id.main_grass)
        Snackbar.make(
            view,
            msg,
            Snackbar.LENGTH_LONG
        ).setAction("okay"){}
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy: $shouldPlay ")

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