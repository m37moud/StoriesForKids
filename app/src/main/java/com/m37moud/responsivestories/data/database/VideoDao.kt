package com.m37moud.responsivestories.data.database

import androidx.room.*
import com.m37moud.responsivestories.data.database.entity.VideoEntity2
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertVideos(videoEntity : VideoEntity2)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideos(videoEntity : VideoEntity2)


    @Query("SELECT * FROM video_table ORDER BY id DESC")
    fun readVideos(): Flow<List<VideoEntity2>>


//    @Delete
//    suspend fun deleteVideo(videoEntity : VideoEntity2)

    @Query("DELETE FROM video_table WHERE id = :tid")
    suspend fun deleteVideo(tid : String)

    @Update(entity = VideoEntity2::class)
    suspend fun updateVideo(videoEntity : VideoEntity2)

    @Query("UPDATE video_table SET updateOk = :video WHERE id = :tid")
    suspend fun updateVideoComplete(tid :String , video : Boolean)



}