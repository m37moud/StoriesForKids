package com.m37moud.responsivestories.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.m37moud.responsivestories.models.VideoModel

class VideosTypeConverter {

    var gson = Gson()
    @TypeConverter
    fun resultToString(result: VideoModel): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(data: String): VideoModel {
        val listType = object : TypeToken<VideoModel>() {}.type
        return gson.fromJson(data, listType)
    }
}