package com.m37moud.responsivestories.util.media

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PodcastEntryPoint {
    fun audioManager(): AudioManager
}