package com.m37moud.responsivestories.data

import com.m37moud.responsivestories.data.database.VideoDao
import com.m37moud.responsivestories.data.database.entity.CategoriesEntity
import com.m37moud.responsivestories.data.database.entity.VideoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val videosDao: VideoDao
) {

    fun readVideos(): Flow<List<VideoEntity>> {
        return videosDao.readVideos()
    }
 fun readVideosWithCategory(categoryName: String): Flow<List<VideoEntity>> {
        return videosDao.readVideosWithCategory(categoryName)
    }

    suspend fun insertVideos(videoEntity: VideoEntity) {
        videosDao.insertVideos(videoEntity)
    }
    suspend fun deleteVideo(id: String) {
        videosDao.deleteVideo(id)
    }

    suspend fun updateVideo(videoEntity: VideoEntity) {
        videosDao.updateVideo(videoEntity)
    }
    suspend fun updateVideoComplete(tid :String , video : Boolean) {
        videosDao.updateVideoComplete(tid , video)
    }

//Categories
    fun readCategories(): Flow<List<CategoriesEntity>> {
        return videosDao.readCategories()
    }

    suspend fun insertCategories(categoriesEntity : CategoriesEntity) {
        videosDao.insertCategories(categoriesEntity)
    }
    suspend fun deleteCategories() {
        videosDao.deleteCategories()
    }
}