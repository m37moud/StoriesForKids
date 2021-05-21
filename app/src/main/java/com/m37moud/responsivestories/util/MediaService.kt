package com.m37moud.responsivestories.util

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log



class MediaService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }



    override fun onCreate(){
        super.onCreate()

        playBackgroundSound()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer.start()
        Log.d("onStartCommand", "onStartCommand: started ")
        return START_STICKY
    }


    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        Log.d("onDestroy", "finish ")
       
    }





     fun playBackgroundSound() {
        var path: String? = null

        try {
            mediaPlayer = MediaPlayer()

            path = "sound/sfx/loop.mp3"

            Log.d("soundmd", "play: " + path)

            val descriptor = assets?.openFd(path)
            if (descriptor != null) {
                mediaPlayer.setDataSource(
                    descriptor.fileDescriptor,
                    descriptor.startOffset,
                    descriptor.length
                )
                descriptor.close()
            }
            mediaPlayer.prepare()
            mediaPlayer.setVolume(1f, 1f)
            mediaPlayer.isLooping = true

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}