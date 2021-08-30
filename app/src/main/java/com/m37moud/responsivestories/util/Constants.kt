package com.m37moud.responsivestories.util

import android.graphics.Color
import java.util.*

class Constants {
    companion object {

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

        const val PREFERENCES_NAME = "story_preferences"


        const val DEFAULT_MEAL_TYPE = ""
        const val DEFAULT_DIET_TYPE = ""


        fun getRandomColor(): Int {
            val rnd = Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(56), rnd.nextInt(256))
        }

    }
}