package com.m37moud.responsivestories.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.m37moud.responsivestories.BuildConfig
import com.m37moud.responsivestories.R

object RemoteConfigUtils {

    private const val TAG = "RemoteConfigUtils"

    private const val NEXT_BUTTON_TEXT = "NEXT_BUTTON_TEXT"
    private const val NEXT_BUTTON_COLOR = "NEXT_BUTTON_COLOR"
    private const val min_version_of_app = "min_version_of_app"
    private const val latest_version_of_app = "latest_version_of_app"
    private const val GOOGLE_PLAY_STATUS = "uploaded_to_google"
    private const val STORE_LINK = "store_link"

    private val DEFAULTS: HashMap<String, Any> =
        hashMapOf(
            NEXT_BUTTON_TEXT to "NEXT",
            NEXT_BUTTON_COLOR to "#0091FF"
        )

    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun init() {
        try {
//            remoteConfig = getFirebaseRemoteConfig(context)
            remoteConfig = getFirebaseRemoteConfig()

        }catch (e :Exception){
            e.stackTrace
            Log.d(TAG, "init: ${e.message}")
        }
    }

//    private fun getFirebaseRemoteConfig(context: Context): FirebaseRemoteConfig {
//        try {
//            FirebaseApp.initializeApp(context)
//
//            remoteConfig =  FirebaseRemoteConfig.getInstance().apply {
//                //set this during development
//                val configSettings = FirebaseRemoteConfigSettings.Builder()
//                    .setMinimumFetchIntervalInSeconds(0)
//                    .build()
//                setConfigSettingsAsync(configSettings)
//                //set this during development
//                setDefaultsAsync(R.xml.remote_config_defaults)
//                fetchAndActivate().addOnCompleteListener { task ->
//                    val updated = task.result
//                    if (task.isSuccessful) {
//                        val updated = task.result
//                        Log.d("TAG", "Config params updated: $updated")
//                    } else {
//                        Log.d("TAG", "Config params updated: $updated")
//                    }
//                }
//            }
//        }catch (e :Exception){
//            e.stackTrace
//            Log.d(TAG, "init: ${e.message}")
//        }
//
//
//        return remoteConfig
//    }

    private fun getFirebaseRemoteConfig(): FirebaseRemoteConfig {

        val remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
//            minimumFetchIntervalInSeconds = 0
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Config params updated: if true")

                minimumFetchIntervalInSeconds = 0 // Kept 0 for quick debug
            } else {
                Log.d(TAG, "Config params updated: if false")

                minimumFetchIntervalInSeconds = 60 * 60 // Change this based on your requirement
            }
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(DEFAULTS)

        remoteConfig.fetchAndActivate().addOnCompleteListener {task ->
            val updated = task.result
                    if (task.isSuccessful) {
                        val updated = task.result
                        Log.d(TAG, "Config params updated: $updated")
                    } else {
                        Log.d(TAG, "Config params updated: $updated")
                    }
        }

        return remoteConfig
    }
    fun getNextButtonText(): String = remoteConfig.getString(NEXT_BUTTON_TEXT)
    fun getNextButtonColor(): String = remoteConfig.getString(NEXT_BUTTON_COLOR)
    fun getMinVersionOfApp(): String = remoteConfig.getString(min_version_of_app)
    fun getLatestVersionOfApp(): String = remoteConfig.getString(latest_version_of_app)
    fun isUploadToGooglePlay(): Boolean = remoteConfig.getBoolean(GOOGLE_PLAY_STATUS)
    fun getOpenLink(): String = remoteConfig.getString(STORE_LINK)

}