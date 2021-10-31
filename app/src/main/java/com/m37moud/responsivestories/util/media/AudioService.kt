package com.m37moud.responsivestories.util.media

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.m37moud.responsivestories.util.Constants.Companion.disableNextSound
import com.m37moud.responsivestories.util.Constants.Companion.disablePreviousSound
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_entered_learen.*
import kotlinx.android.synthetic.main.folder_container.*
import java.io.File
import java.io.IOException
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
        tracks = Random().nextInt(5) + 1

        createPlayer()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
//        mediaPlayer?.prepare()
//        mediaPlayer?.start()
//        playMusic()
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
            mediaPlayer?.setOnCompletionListener {

                nextMusic()
            }

            if (mediaPlayer != null) {
                mediaPlayer?.isLooping = false
                mediaPlayer?.setVolume(1f, 1f)
            }
            Log.d("audio", "playBackgroundSound: $tracks ")

            path = "sound/sfx/loop/loop$tracks.mp3"
//            path = "sound/sfx/loop/loop"

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
        Log.d("audio", "pauseMusic: ")
        try {
            mediaPlayer?.let {
//           it.prepare()

//                if (!it.isPlaying)
                it.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun pauseMusic() {
        Log.d("audio", "pauseMusic: ")
        if (mediaPlayer != null) {

            try {
                if (mediaPlayer!!.isPlaying && mediaPlayer != null) {
                    mediaPlayer?.pause()
                    lengthPostition = mediaPlayer!!.currentPosition
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun resumeMusic() {
        Log.d("audio", "resumeMusic: ")

        try {
//            if (mediaPlayer != null) {
            if (!mediaPlayer!!.isPlaying && mediaPlayer != null) {
                mediaPlayer!!.seekTo(lengthPostition)
                mediaPlayer!!.start()
            }
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun nextMusic() {
        Log.d("audio", "nextMusic: ")

        if (tracks < 6) {
            tracks++

        }
        if (tracks == 6) {
            disableNextSound = true
            tracks = 0
        }

        try {
            if (mediaPlayer != null) {
                stopMusic()
            }

            if (mediaPlayer == null) {
                createPlayer()
                playMusic()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun previousMusic() {
        if (tracks > 0)
            tracks--

        if (tracks == 0) {
            disablePreviousSound = true
            tracks = 6

        }



        Log.d("audio", "previousMusic: ")

        try {
            if (mediaPlayer != null) {
                stopMusic()
            }

            if (mediaPlayer == null) {
                createPlayer()
                playMusic()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopMusic() {
        Log.d("audio", "stopMusic: ")
        Log.d("audio", "pauseMusic: ")
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.stop()

                }
                mediaPlayer?.release()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun checkTrack(): Boolean {
        if (tracks == 6) return disableNextSound
        if (tracks == 0) return disablePreviousSound

        return false
    }


    fun getAllFilesInAssetByExtension(
        path: String?,
        extension: String?
    ): Array<String>? {
        try {
            val files = path?.let { this.assets.list(it) }
            if (TextUtils.isEmpty(extension)) {
                return files
            }
            val filesWithExtension: MutableList<String> =
                ArrayList()
            for (file in files!!) {
                if (file.endsWith(extension!!)) {
                    filesWithExtension.add(file)
                }
            }
            return filesWithExtension.toTypedArray()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return null
    }

}