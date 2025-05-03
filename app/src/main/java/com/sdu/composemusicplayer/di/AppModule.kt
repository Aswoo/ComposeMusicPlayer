package com.sdu.composemusicplayer.di

import android.content.Context
import com.sdu.composemusicplayer.data.music.MusicDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMusicDB(
        @ApplicationContext context: Context,
    ): MusicDB = MusicDB.getInstance(context)

    @Provides
    @Singleton
    fun provideMusicDao(
        musicDB: MusicDB
    ) = musicDB.musicDao() 

    @Singleton
    @Provides
    fun provideLyricsDao(
        musicDB: MusicDB
    ) = musicDB.lyricsDao()

    @Singleton
    @Provides
    fun provideQueueDao(
        musicDB: MusicDB
    ) = musicDB.queueDao()
}
