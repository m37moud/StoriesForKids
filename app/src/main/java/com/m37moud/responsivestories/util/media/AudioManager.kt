package com.m37moud.responsivestories.util.media

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {


    private var audioService: AudioService? = null
    private var bound: Boolean = false

    // Defines callbacks for service binding, passed to bindService()

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to AudioService, cast the IBinder and get AudioService instance

            val binder = service as AudioService.AudioBinder
            audioService = binder.getService()

            Log.d("audio", "onServiceConnected:  $audioService")

            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.d("audio", "onServiceDisconnected:  $audioService")

            bound = false
        }
    }


    fun getAudioService(): AudioService? {
        Log.d("audio", "getAudioService: ")

        return if (this.bound) {
            Log.d("audio", "if true: ")

            this.audioService
        } else {
            Log.d("audio", "if false: ")

            null
        }

//       return this.audioService
    }

    fun doBindService() {
        Intent(context, AudioService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        Log.d("audio", "doBindService: ")
    }

    fun doUnbindService() {

        if (bound) {
            context.unbindService(connection)
            bound = false
        }
    }

    init {

//        val intent =
        Intent(context, AudioService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
//        context.startService(intent)

        Log.d("audio", "init: ")
    }

}