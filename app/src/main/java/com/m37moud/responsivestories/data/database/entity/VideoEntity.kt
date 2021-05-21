package com.m37moud.responsivestories.data.database.entity

import androidx.room.*
import com.m37moud.responsivestories.models.VideoModel
import com.m37moud.responsivestories.util.Constants.Companion.VIDEOS_TABLE

@Entity(tableName = VIDEOS_TABLE , indices = [Index(value = ["videos"], unique = true)])
class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "videos")
    var videos: VideoModel
)
