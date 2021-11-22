package com.m37moud.responsivestories.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.responsivestories.R
import kotlinx.android.synthetic.main.activity_start.*
import java.util.*

class Constants {
    companion object {

         var showLoading = false
         var activateSetting = false
         var disableNextSound = false
         var disablePreviousSound = false
         var doOnce = false

        //Ads
         var showAdsFromRemoteConfig: Boolean = false
        var addRewardAds : String? = ""
        var bannerAds : String? = ""
        var interstitialAds : String? = ""



        val img = listOf<String>("animals", "colors", "shapes", "numbers", "alphabets")

        const val RESOURCE = "android.resource://com.m37moud.responsivestories/drawable/"

        const val USER_AGENT = "ResponsiveStory"

        const val EXO_VIDEO_TITLE = "exo_video_title"
        const val EXO_VIDEO_ID = "exo_video_status_id"

        // ======================== Exo Download Action ===================

        const val EXO_DOWNLOAD_ACTION_START = "EXO_DOWNLOAD_START"
        const val EXO_DOWNLOAD_ACTION_PAUSE = "EXO_DOWNLOAD_PAUSE"
        const val EXO_DOWNLOAD_ACTION_CANCEL = "EXO_DOWNLOAD_CANCEL"


        const val DATABASE_NAME = "videos_database"
        const val VIDEOS_TABLE = "videos_table"
        const val VIDEO_TABLE = "video_table"
        const val CATEGORY_TABLE = "category_table"



        const val PREFERENCES_BACK_ONLINE = "backOnline"
        const val PREFERENCES_DOWNLOAD_STATUS = "downloadStatus"
        const val PREFERENCES_LOADING_STATUS = "loadingStatus"
        const val PREFERENCES_ExitBottom_STATUS = "exit"




        const val PREFERENCES_NAME = "story_preferences"


        const val PREFERENCES_CATEGORY_TYPE = "categoryType"
        const val PREFERENCES_CATEGORY_TYPE_ID = "categoryTypeId"


        const val DEFAULT_CATEGORY_TYPE = ""


        fun getRandomColor(): Int {
            val rnd = Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(56), rnd.nextInt(256))
        }
         fun initBackgroundColor(frame: FrameLayout? , context :Context) {
             val androidColors = context.resources.getIntArray(R.array.androidcolors)
             val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
             frame!!.setBackgroundColor(randomAndroidColor)

//            val androidColors = context.resources.getStringArray(R.array.androidcolors)
//            val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
//            frame!!.setBackgroundColor(randomAndroidColor.toInt())

//             val generator: ColorGenerator = ColorGenerator.MATERIAL
//
//             val color: Int = generator.getRandomColor()
//             frame.background = color
        }

        fun initBackgroundColor(frame: RelativeLayout? , context :Context) {
            val androidColors = context.resources.getIntArray(R.array.androidcolors)
            val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
            frame!!.setBackgroundColor(randomAndroidColor)

        }



         fun clickSound(context :Context) {
            var path: String?

            try {
//                val newName = removeLastChar(name)

                path = "sound/sfx/click.mp3"

                Log.d("soundmd", "play: " + path)
                val mediaPlayer = MediaPlayer()

                val descriptor = context.assets?.openFd(path)
                if (descriptor != null) {
                    mediaPlayer.setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )

                    descriptor.close()
                }

                mediaPlayer.prepare()
                mediaPlayer.setVolume(2f, 2f)
                mediaPlayer.isLooping = false
                mediaPlayer.start()
            } catch (e: Exception) {
                Log.d("soundmd", "play: " + e)
                e.printStackTrace()
            }

        }
        fun fabOpenSound(context :Context) {
            var path: String?

            try {
//                val newName = removeLastChar(name)

                path = "sound/sfx/sw1.mp3"

                Log.d("soundmd", "play: " + path)
                val mediaPlayer = MediaPlayer()

                val descriptor = context.assets?.openFd(path)
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
                mediaPlayer.isLooping = false
                mediaPlayer.start()
            } catch (e: Exception) {
                Log.d("soundmd", "play: " + e)
                e.printStackTrace()
            }

        }
        fun fabCloseSound(context :Context) {
            var path: String?

            try {
//                val newName = removeLastChar(name)

                path = "sound/sfx/sw2.mp3"

                Log.d("soundmd", "play: " + path)
                val mediaPlayer = MediaPlayer()

                val descriptor = context.assets?.openFd(path)
                if (descriptor != null) {
                    mediaPlayer.setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )

                    descriptor.close()
                }

                mediaPlayer.prepare()
                mediaPlayer.setVolume(2f, 2f)
                mediaPlayer.isLooping = false
                mediaPlayer.start()
            } catch (e: Exception) {
                Log.d("soundmd", "play: " + e)
                e.printStackTrace()
            }

        }

        fun buttonAppearSound(context :Context) {
            var path: String?

            try {
//                val newName = removeLastChar(name)

                path = "sound/sfx/pop2.mp3"

                Log.d("soundmd", "play: " + path)
                val mediaPlayer = MediaPlayer()

                val descriptor = context.assets?.openFd(path)
                if (descriptor != null) {
                    mediaPlayer.setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )

                    descriptor.close()
                }

                mediaPlayer.prepare()
                mediaPlayer.setVolume(2f, 2f)
                mediaPlayer.isLooping = false
                mediaPlayer.start()
            } catch (e: Exception) {
                Log.d("soundmd", "play: " + e)
                e.printStackTrace()
            }

        }

        fun removeLastChar(str: String?): String? {
            var str = str
            if (str != null && str.isNotEmpty() && str[str.length - 1] == 's') {
                str = str.substring(0, str.length - 1)
            }
            return str
        }

    }

    object Listeners {
        val onTouch = View.OnTouchListener { v: View, p1: MotionEvent ->
            //Do anything with view here and check event type

            if (p1?.action == MotionEvent.ACTION_DOWN) {
                v?.animate()?.setInterpolator(DecelerateInterpolator())?.scaleX(.7f)?.scaleY(.7f)
            } else if (p1?.action == MotionEvent.ACTION_UP) {
                v?.animate()?.setInterpolator(OvershootInterpolator(10f))?.scaleX(1f)?.scaleY(1f)
            }
            return@OnTouchListener false
        }
    }
}