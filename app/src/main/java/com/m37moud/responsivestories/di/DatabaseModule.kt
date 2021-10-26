package com.m37moud.responsivestories.di

import android.content.Context
import android.content.ServiceConnection
import androidx.room.Room
import com.m37moud.responsivestories.data.database.VideoDatabase
import com.m37moud.responsivestories.util.Constants.Companion.DATABASE_NAME
import com.m37moud.responsivestories.util.media.AudioManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {


    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        VideoDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: VideoDatabase) = database.videosDao()

//    @Singleton
//    @Provides
//    fun provideAudioManager( a: AudioManager) = a.doBindService()
}