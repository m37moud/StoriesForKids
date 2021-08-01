package com.m37moud.responsivestories.data

import com.m37moud.responsivestories.data.database.VideoDao
import com.m37moud.responsivestories.data.database.entity.VideoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val videosDao: VideoDao
) {

    fun readVideos(): Flow<List<VideoEntity>> {
        return videosDao.readVideos()
    }

    suspend fun insertVideos(videoEntity: VideoEntity) {
        videosDao.insertVideos(videoEntity)
    }

    suspend fun deleteVideo(videoEntity: VideoEntity) {
        videosDao.deleteVideo(videoEntity)
    }

    suspend fun updateVideo(videoEntity: VideoEntity) {
        videosDao.updateVideo(videoEntity)
    }
}