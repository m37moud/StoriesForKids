package com.m37moud.responsivestories.util

class Constants {
    companion object {

        val img = listOf<String>("animals", "colors", "shapes", "numbers", "alphabet")

        const val RESOURCE = "android.resource://com.m37moud.responsivestories/drawable/"
//        const val mediaPlayer: MediaPlayer = TODO()

        const val USER_AGENT = "ResponsiveStory"

        const val EXO_VIDEO_TITLE = "exo_video_title"
        const val EXO_VIDEO_ID = "exo_video_status_id"

        // ======================== Exo Download Action ===================
        const val EXO_DOWNLOAD_ACTION_START = "EXO_DOWNLOAD_START"
        const val EXO_DOWNLOAD_ACTION_PAUSE = "EXO_DOWNLOAD_PAUSE"
        const val EXO_DOWNLOAD_ACTION_CANCEL = "EXO_DOWNLOAD_CANCEL"


        const val DATABASE_NAME = "videos_database"
        const val VIDEOS_TABLE = "videos_table"



        const val PREFERENCES_BACK_ONLINE = "backOnline"
        const val PREFERENCES_DOWNLOAD_STATUS = "downloadStatus"

        const val PREFERENCES_NAME = "story_preferences"




    }
}