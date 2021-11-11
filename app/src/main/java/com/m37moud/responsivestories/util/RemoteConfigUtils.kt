package com.m37moud.responsivestories.util

import android.util.Log
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfigUtils {

    private const val TAG = "RemoteConfigUtils"

    private const val NEXT_BUTTON_TEXT = "next_button_text"
    private const val NEXT_BUTTON_COLOR = "next_button_color"

    private val DEFAULTS: HashMap<String, Any> =
        hashMapOf(
            NEXT_BUTTON_TEXT to "NEXT",
            NEXT_BUTTON_COLOR to "#0091FF"
        )

    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun init() {
        remoteConfig = getFirebaseRemoteConfig()
    }

    private fun getFirebaseRemoteConfig(): FirebaseRemoteConfig {

        val remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
            if (BuildConfig.DEBUG) {
                minimumFetchIntervalInSeconds = 10 // Kept 0 for quick debug
            } else {
                minimumFetchIntervalInSeconds = 60 * 60 // Change this based on your requirement
            }
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.setDefaultsAsync(DEFAULTS)

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            Log.d(TAG, "addOnCompleteListener")
        }

        return remoteConfig
    }

    fun getNextButtonText(): String = remoteConfig[NEXT_BUTTON_TEXT].asString()

    fun getNextButtonColor(): String = remoteConfig.getString(NEXT_BUTTON_COLOR)

}