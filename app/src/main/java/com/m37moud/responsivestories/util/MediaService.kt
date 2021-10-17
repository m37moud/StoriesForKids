package com.m37moud.responsivestories.util

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import java.util.*


class MediaService : Service() {
    private  var tracks : Int = 0
    private lateinit var mediaPlayer: MediaPlayer
    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }



    override fun onCreate(){
        super.onCreate()
        tracks = Random().nextInt(5)+1
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
         var path: String?

         try {
            mediaPlayer = MediaPlayer()
//             var tracks = Random().nextInt(6)+1
             Log.d("TAG", "playBackgroundSound: $tracks ")

            path = "sound/sfx/loop$tracks.mp3"

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