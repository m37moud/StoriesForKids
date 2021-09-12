package com.m37moud.responsivestories.util

import android.content.Context
import android.graphics.Color
import android.widget.FrameLayout
import com.m37moud.responsivestories.R
import kotlinx.android.synthetic.main.activity_start.*
import java.util.*

class Constants {
    companion object {

         var showLoading = false


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

        const val PREFERENCES_NAME = "story_preferences"


        const val PREFERENCES_CATEGORY_TYPE = "categoryType"
        const val PREFERENCES_CATEGORY_TYPE_ID = "categoryTypeId"


        const val DEFAULT_CATEGORY_TYPE = "All"


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



    }
}