package com.m37moud.responsivestories.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.m37moud.responsivestories.data.database.entity.CategoriesEntity
import com.m37moud.responsivestories.data.database.entity.VideoEntity

@Database(
    entities = [VideoEntity::class , CategoriesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(VideosTypeConverter::class)
abstract class VideoDatabase : RoomDatabase() {

    abstract fun videosDao(): VideoDao


}