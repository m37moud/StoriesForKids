package com.m37moud.responsivestories.data.database

import androidx.room.*
import com.m37moud.responsivestories.data.database.entity.VideoEntity
import com.m37moud.responsivestories.models.VideoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideos(videoEntity : VideoEntity)


    @Query("SELECT * FROM videos_table ORDER BY id ASC")
    fun readVideos(): Flow<List<VideoEntity>>


    @Delete
    suspend fun deleteVideo(videoEntity : VideoEntity)

    @Update(entity = VideoEntity::class)
    suspend fun updateVideo(videoEntity : VideoEntity)

    @Query("UPDATE videos_table SET update = :video WHERE id = :tid")
    suspend fun updateVideoComplete(tid :Int , video : VideoModel)



}