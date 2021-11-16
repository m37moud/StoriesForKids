package com.m37moud.responsivestories.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.m37moud.responsivestories.R

object RemoteConfigUtils {

    private const val TAG = "RemoteConfigUtils"

    private const val NEXT_BUTTON_TEXT = "NEXT_BUTTON_TEXT"
    private const val NEXT_BUTTON_COLOR = "NEXT_BUTTON_COLOR"

    private val DEFAULTS: HashMap<String, Any> =
        hashMapOf(
            NEXT_BUTTON_TEXT to "NEXT",
            NEXT_BUTTON_COLOR to "#0091FF"
        )

    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun init(context: Context) {
        remoteConfig = getFirebaseRemoteConfig(context)
    }

    private fun getFirebaseRemoteConfig(context: Context): FirebaseRemoteConfig {
        FirebaseApp.initializeApp(context)


        val remoteConfig =  FirebaseRemoteConfig.getInstance().apply {
            //set this during development
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            setConfigSettingsAsync(configSettings)
            //set this during development
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate().addOnCompleteListener { task ->
                val updated = task.result
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("TAG", "Config params updated: $updated")
                } else {
                    Log.d("TAG", "Config params updated: $updated")
                }
            }
        }

        return remoteConfig
    }

    fun getNextButtonText(): String = remoteConfig.getString(NEXT_BUTTON_TEXT)
    fun getNextButtonColor(): String = remoteConfig.getString(NEXT_BUTTON_COLOR)

}