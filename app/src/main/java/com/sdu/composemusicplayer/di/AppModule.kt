package com.sdu.composemusicplayer.di

import android.content.Context
import com.sdu.composemusicplayer.core.database.LyricsRepositoryImpl
import com.sdu.composemusicplayer.core.database.MusicDB
import com.sdu.composemusicplayer.core.database.MusicRepositoryImpl
import com.sdu.composemusicplayer.core.database.PlaylistsRepositoryImpl
import com.sdu.composemusicplayer.core.database.QueueRepositoryImpl
import com.sdu.composemusicplayer.core.database.dao.LyricsDao
import com.sdu.composemusicplayer.core.database.dao.MusicDao
import com.sdu.composemusicplayer.core.database.dao.PlaylistDao
import com.sdu.composemusicplayer.core.database.dao.QueueDao
import com.sdu.composemusicplayer.core.media.MediaRepositoryImpl
import com.sdu.composemusicplayer.domain.repository.LyricsRepository
import com.sdu.composemusicplayer.domain.repository.MediaRepository
import com.sdu.composemusicplayer.domain.repository.MusicRepository
import com.sdu.composemusicplayer.domain.repository.PlaylistsRepository
import com.sdu.composemusicplayer.domain.repository.QueueRepository
import com.sdu.composemusicplayer.network.data.LyricsSource
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
    fun provideMusicDao(musicDB: MusicDB) = musicDB.musicDao()

    @Singleton
    @Provides
    fun provideLyricsDao(musicDB: MusicDB) = musicDB.lyricsDao()

    @Singleton
    @Provides
    fun provideQueueDao(musicDB: MusicDB) = musicDB.queueDao()

    @Singleton
    @Provides
    fun providePlayListDao(musicDB: MusicDB) = musicDB.playListDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providePlaylistsRepository(
        playlistsDao: PlaylistDao,
        musicRepository: MusicRepository,
    ): PlaylistsRepository = PlaylistsRepositoryImpl(playlistsDao, musicRepository)

    @Provides
    @Singleton
    fun provideMusicRepository(
        musicDao: MusicDao,
        @ApplicationContext context: Context,
    ): MusicRepository = MusicRepositoryImpl(musicDao, context)

    @Provides
    @Singleton
    fun provideQueueRepository(queueDao: QueueDao): QueueRepository = QueueRepositoryImpl(queueDao)

    @Provides
    @Singleton
    fun provideLyricsRepository(
        @ApplicationContext context: Context,
        lyricsDataSource: LyricsSource,
        lyricsDao: LyricsDao,
    ): LyricsRepository = LyricsRepositoryImpl(context, lyricsDataSource, lyricsDao)

    @Provides
    @Singleton
    fun provideMediaRepository(
        @ApplicationContext context: Context,
    ): MediaRepository = MediaRepositoryImpl(context)
}
