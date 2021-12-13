package com.m37moud.responsivestories

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.HiltAndroidApp
private const val TAG = "MyApplication"
@HiltAndroidApp
class MyApplication: Application() {
//
    override fun onCreate() {
        super.onCreate()


//    initFirebaseRemoteConfig()
    }

    private fun initFirebaseRemoteConfig() {
       try{

           FirebaseApp.initializeApp(this)
           FirebaseRemoteConfig.getInstance().apply {
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
       }catch (e : Exception){
           e.stackTrace
           Log.d(TAG, "initFirebaseRemoteConfig: $e")
       }

    }



}