package com.m37moud.responsivestories

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.m37moud.responsivestories.util.FirebaseService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.m37moud.responsivestories.ui.activities.learn.LearnActivity
import com.m37moud.responsivestories.ui.activities.story.StoryActivity
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

const val TOPIC = "/topics/myTopic2"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    //fab button menu
    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this , R.anim.rotate_open_anim) }
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this , R.anim.rotate_close_anim) }
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(this , R.anim.from_bottom_anim) }
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this , R.anim.to_bottom_anim) }
    private var clicked = false

    private lateinit var videosViewModel: VideosViewModel

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        //fab menu
        open_menu_fab.setOnClickListener { onAddButtonClicked() }

        youtube_fab.setOnClickListener { getOpenYoutubeIntent() }

        facebook_fab.setOnClickListener { getOpenFacebookIntent() }
        gmail_fab.setOnClickListener { getOpenMailIntent() }

        //start story activity
        story_card_view.setOnClickListener {
            startActivity(Intent(this@MainActivity, StoryActivity::class.java))
//            finish()
        }

        learn_card_view.setOnClickListener {
            startActivity(Intent(this@MainActivity, LearnActivity::class.java))
//            finish()
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
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
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

                Toast.makeText(baseContext, "msg", Toast.LENGTH_SHORT).show()
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

    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked : Boolean) {
      if(!clicked){
          facebook_fab.startAnimation(fromBottom)
          gmail_fab.startAnimation(fromBottom)
          youtube_fab.startAnimation(fromBottom)
          open_menu_fab.startAnimation(rotateOpen)
      }else{
          facebook_fab.startAnimation(toBottom)
          gmail_fab.startAnimation(toBottom)
          youtube_fab.startAnimation(toBottom)
          open_menu_fab.startAnimation(rotateClose)
      }
    }

    private fun setVisibility(clicked : Boolean) {
       if(!clicked){
           youtube_fab.visibility = View.VISIBLE
           gmail_fab.visibility = View.VISIBLE
           facebook_fab.visibility = View.VISIBLE
       }else{
           youtube_fab.visibility = View.INVISIBLE
           gmail_fab.visibility = View.INVISIBLE
           facebook_fab.visibility = View.INVISIBLE
       }
    }
    private fun setClickable(clicked : Boolean) {
        if(!clicked){
            youtube_fab.isClickable = true
            gmail_fab.isClickable = true
            facebook_fab.isClickable = true
        }else{
            youtube_fab.isClickable = false
            gmail_fab.isClickable = false
            facebook_fab.isClickable = false
        }
    }

    override fun onStart() {

        videosViewModel.saveDownloadStatus(false)
        Log.d("MainActivity", "onStart: ")
        super.onStart()
    }

    override fun onDestroy() {
//        when app end download status = false
        Log.d("mainAcc", "onDestroy! -> saveDownloadStatus = false")
        videosViewModel.saveDownloadStatus(false)
        super.onDestroy()
    }

    fun replayMainButton(view: View) {}
    fun homeMainButton(view: View) {}

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

        Intent(Intent.ACTION_SENDTO ).apply {
            type = "text/plain"
//            type = "message/rfc822"
            data = Uri.parse("mailto:m37moud00@gmail.com")
            putExtra(Intent.EXTRA_TEXT, "that is a great app ")
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name).plus(" App "))
        }.also { readyIntent ->
            startActivity(Intent.createChooser(readyIntent,"Send feedback"))
        }

    } catch (e: Exception) {
        Log.e("TAG", "getOpenGMailIntent: ", e)
        Intent(
            Intent.ACTION_SEND,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ?sub_confirmation=1")
        )
    }

}