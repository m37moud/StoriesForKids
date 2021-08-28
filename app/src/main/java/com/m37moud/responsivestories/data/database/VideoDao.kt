package com.m37moud.responsivestories.data.database

import androidx.room.*
import com.m37moud.responsivestories.data.database.entity.CategoriesEntity
import com.m37moud.responsivestories.data.database.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideos(videoEntity : VideoEntity)

    @Query("SELECT * FROM video_table ORDER BY id DESC")
    fun readVideos(): Flow<List<VideoEntity>>


    @Query("DELETE FROM video_table WHERE id = :tid")
    suspend fun deleteVideo(tid : String)

    @Update(entity = VideoEntity::class)
    suspend fun updateVideo(videoEntity : VideoEntity)

    @Query("UPDATE video_table SET updateOk = :video WHERE id = :tid")
    suspend fun updateVideoComplete(tid :String , video : Boolean)
//Categories
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categoriesEntity : CategoriesEntity)

    @Query("SELECT * FROM category_table ORDER BY categoryId ASC")
    fun readCategories(): Flow<List<CategoriesEntity>>

}