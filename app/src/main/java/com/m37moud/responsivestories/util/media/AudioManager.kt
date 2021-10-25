package com.m37moud.responsivestories.util.media

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            bound = false
        }
    }


    fun getAudioService(): AudioService? {
        return if (this.bound) {
            this.audioService
        } else {
            null
        }
    }

    init {
        Intent(context, AudioService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}