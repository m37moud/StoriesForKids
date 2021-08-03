package com.m37moud.responsivestories.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.m37moud.responsivestories.data.database.entity.VideoEntity
import com.m37moud.responsivestories.data.database.entity.VideoEntity2

@Database(
    entities = [VideoEntity2::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(VideosTypeConverter::class)
abstract class VideoDatabase : RoomDatabase() {

    abstract fun videosDao(): VideoDao


}