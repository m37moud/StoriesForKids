package com.m37moud.responsivestories

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.m37moud.responsivestories.util.MyFirebaseMessagingService
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint

const val TOPIC = "/topics/myTopic2"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var videosViewModel: VideosViewModel

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics


        // Get token
        // [START log_reg_token]
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("getToken", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            val referenceVideos = FirebaseDatabase.getInstance().getReference("RG_token")
            referenceVideos.child("client").setValue(token)
                .addOnSuccessListener {
                    Log.d("Fetching", "sendRegistrationToServer :  successful ")
                }
                .addOnFailureListener { e ->
                    Log.d("Fetching", " sendRegistrationToServer :  err "+e.message.toString())

                    //failed to add info to database
                }

        })
        // [END log_reg_token]


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

    override fun onStart() {
        videosViewModel.saveDownloadStatus(false)
        super.onStart()
    }

    override fun onDestroy() {
//        when app end download status = false
        Log.d("mainAcc", "onDestroy! -> saveDownloadStatus = false")
        videosViewModel.saveDownloadStatus(false)
        super.onDestroy()
    }
}