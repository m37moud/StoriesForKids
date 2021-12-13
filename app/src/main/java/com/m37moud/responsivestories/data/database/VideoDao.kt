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

    @Query("SELECT * FROM video_table WHERE videoCategory = :categoryName  ORDER BY id DESC")
    fun readVideosWithCategory(categoryName: String): Flow<List<VideoEntity>>


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

    @Query("SELECT DISTINCT categoryName , categoryId , categoryImage from category_table INNER JOIN video_table ON video_table.videoCategory = category_table.categoryName ORDER BY category_table.categoryId")
    fun readCategoriesFromVideos(): Flow<List<CategoriesEntity>>

//    @Query("SELECT category_table.categoryId from category_table INNER JOIN video_table ON video_table.videoCategory = category_table.categoryName ORDER BY category_table.categoryId")
//    fun readArabicCategories(): Flow<List<CategoriesEntity>>

    @Query("DELETE FROM category_table ")
    suspend fun deleteCategories()

}