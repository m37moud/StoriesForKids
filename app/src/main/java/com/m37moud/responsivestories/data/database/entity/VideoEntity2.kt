package com.m37moud.responsivestories.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.m37moud.responsivestories.util.Constants

@Entity(tableName = Constants.VIDEO_TABLE)

class VideoEntity2 (
    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "timestamp")
    var timestamp: String? = null,

    @ColumnInfo(name = "videoUri")
    var videoUri: String? = null,

    @ColumnInfo(name = "videoSlide")
    var videoSlide: String? = null,

    @ColumnInfo(name = "videoThumb")
    var videoThumb: String? = null,

    @ColumnInfo(name = "videoCategory")
    var videoCategory: String? = null,

    @ColumnInfo(name = "videoDescription")
    var videoDescription: String? = null,

    @ColumnInfo(name = "videoType")
    var videoType: String? = null,

    @ColumnInfo(name = "updateOk")
    var update: Boolean = false


)