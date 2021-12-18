package com.m37moud.responsivestories

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.HiltAndroidApp
private const val TAG = "MyApplication"
@HiltAndroidApp
class MyApplication: Application()