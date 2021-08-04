package com.m37moud.responsivestories.data

import com.m37moud.responsivestories.data.database.VideoDao
import com.m37moud.responsivestories.data.database.entity.VideoEntity2
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val videosDao: VideoDao
) {

    fun readVideos(): Flow<List<VideoEntity2>> {
        return videosDao.readVideos()
    }

    suspend fun insertVideos(videoEntity: VideoEntity2) {
        videosDao.insertVideos(videoEntity)
    }

//    suspend fun deleteVideo(videoEntity: VideoEntity2) {
//        videosDao.deleteVideo(videoEntity)
//    }
    suspend fun deleteVideo(id: String) {
        videosDao.deleteVideo(id)
    }

    suspend fun updateVideo(videoEntity: VideoEntity2) {
        videosDao.updateVideo(videoEntity)
    }
    suspend fun updateVideoComplete(tid :Int , video : Boolean) {
        videosDao.updateVideoComplete(tid , video)
    }
}