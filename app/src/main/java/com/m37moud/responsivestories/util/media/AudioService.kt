package com.m37moud.responsivestories.util.media

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AudioService @Inject constructor() : Service(), MediaPlayer.OnErrorListener {
    private var path: String? = ""
    private var tracks: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private val binder = AudioBinder()
    private var lengthPostition = 0


    inner class AudioBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }


    override fun onBind(p0: Intent?): IBinder? {
        Log.d("audio", "onBind: ")
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        return false
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        // Create the player when the service is created
        createPlayer()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
//        mediaPlayer?.prepare()
//        mediaPlayer?.start()
        playMusic()
        return START_STICKY
    }

    override fun onDestroy() {
        stopSelf()
        // Don't forget to release Player when the service is destroyed
        releasePlayer()
        super.onDestroy()
    }

    private fun releasePlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } finally {
                mediaPlayer = null

            }
        }
    }


    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show()
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()

            } finally {
                mediaPlayer = null
            }

        }
        return false
    }

    private fun createPlayer() {

        try {

            mediaPlayer = MediaPlayer()
            mediaPlayer?.setOnErrorListener(this)

            if (mediaPlayer != null) {
                mediaPlayer?.isLooping = true
                mediaPlayer?.setVolume(1f, 1f)
            }
            tracks = Random().nextInt(5) + 1
            Log.d("audio", "playBackgroundSound: $tracks ")

            path = "sound/sfx/loop/loop$tracks.mp3"

            Log.d("audio", "play: " + path)

            val descriptor = assets?.openFd(path!!)
            if (descriptor != null) {
                mediaPlayer?.setDataSource(
                    descriptor.fileDescriptor,
                    descriptor.startOffset,
                    descriptor.length
                )
                descriptor.close()
            }
            mediaPlayer?.prepare()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playMusic() {
        Log.d("audio", "playMusic: ")
        mediaPlayer?.let {
//           it.prepare()

            if (!it.isPlaying)
                it.start()
        }
    }


    fun pauseMusic() {
        Log.d("audio", "pauseMusic: ")

        if (mediaPlayer!!.isPlaying && mediaPlayer != null) {
            mediaPlayer?.pause()
            lengthPostition = mediaPlayer!!.currentPosition
        }
    }

    fun resumeMusic() {
        Log.d("audio", "resumeMusic: ")

        try {
            if (!mediaPlayer!!.isPlaying && mediaPlayer != null) {
                mediaPlayer!!.seekTo(lengthPostition)
                mediaPlayer!!.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopMusic() {
        Log.d("audio", "stopMusic: ")

        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer?.stop()

            }
            mediaPlayer?.release()
            mediaPlayer = null
        }

    }

}