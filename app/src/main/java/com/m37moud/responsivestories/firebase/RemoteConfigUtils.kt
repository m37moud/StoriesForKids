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

    private const val min_version_of_app = "min_version_of_app"
    private const val latest_version_of_app = "latest_version_of_app"
    private const val GOOGLE_PLAY_STATUS = "uploaded_to_google"
    private const val STORE_LINK = "store_link"

    private val DEFAULTS: HashMap<String, Any> =
        hashMapOf(

            min_version_of_app to "1.0",
            latest_version_of_app to "1.0",
            GOOGLE_PLAY_STATUS to false ,
            STORE_LINK to "http://play.google.com/store/apps/details?id="

        )

    private var remoteConfig: FirebaseRemoteConfig? = null

    fun init() {
        try {
//            remoteConfig = getFirebaseRemoteConfig(context)
            remoteConfig = getFirebaseRemoteConfig()

        } catch (e: Exception) {
            e.stackTrace
            Log.d(TAG, "init: ${e.message}")
        }
    }


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

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
//            val updated = task.result
            if (task.isSuccessful) {
                val updated = task.result
                Log.d(TAG, "Config params updated: $updated")
            } else {
                Log.d(TAG, "Config params updated: ${task.exception}")
            }
        }

        return remoteConfig
    }



    fun getMinVersionOfApp(): String = try {
        remoteConfig!!.getString(min_version_of_app)
    } catch (e: Exception) {
        e.printStackTrace()
        "1.0"
    }

    fun getLatestVersionOfApp(): String = try {
        remoteConfig!!.getString(latest_version_of_app)
    } catch (e: Exception) {
        e.printStackTrace()
        "1.0"
    }

    fun isUploadToGooglePlay(): Boolean = try {
        remoteConfig!!.getBoolean(GOOGLE_PLAY_STATUS)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getOpenLink(): String = try {
        remoteConfig!!.getString(STORE_LINK)
    } catch (e: Exception) {
        e.printStackTrace()
        "http://play.google.com/store/apps/details?id="
    }

}