@file:Suppress("PrintStackTrace")
package com.sdu.composemusicplayer.core.database

import android.content.Context
import android.net.Uri
import com.sdu.composemusicplayer.core.database.dao.LyricsDao
import com.sdu.composemusicplayer.core.database.entity.LyricsEntity
import com.sdu.composemusicplayer.core.database.model.LyricsResult
import com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource
import com.sdu.composemusicplayer.core.model.lyrics.PlainLyrics
import com.sdu.composemusicplayer.core.model.lyrics.SynchronizedLyrics
import com.sdu.composemusicplayer.network.data.LyricsSource
import com.sdu.composemusicplayer.network.model.NotFoundException
import com.sdu.composemusicplayer.utils.MusicUtil.getSongPath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import com.sdu.composemusicplayer.domain.repository.LyricsRepository as LyricsRepositoryContract

@Singleton
class LyricsRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val lyricsDataSource: LyricsSource,
        private val lyricsDao: LyricsDao,
    ) : LyricsRepositoryContract {
        /**
         * Given a song's URI, fetches its lyrics.
         *
         * It searches for lyrics in the following order:
         * 1. Embedded lyrics in the audio file's metadata (tags).
         * 2. Cached lyrics in the local database.
         * 3. Fetches lyrics from an external API service as a last resort.
         */
        override suspend fun getLyrics(
            uri: Uri,
            title: String,
            album: String,
            artist: String,
            durationSeconds: Int,
        ): LyricsResult = withContext(Dispatchers.IO) {
            val audioFile = File(getSongPath(context, uri))
            val tags = AudioFileIO().readFile(audioFile).tagOrCreateAndSetDefault

            getLyricsFromMetadata(tags)?.let { return@withContext it }
            getLyricsFromDatabase(title, album, artist)?.let { return@withContext it }
            getLyricsFromApi(title, album, artist, durationSeconds)
        }

        private fun getLyricsFromMetadata(tags: Tag): LyricsResult? {
            val lyrics = tags.getFirstField(FieldKey.LYRICS)?.toString() ?: return null
            val syncedLyrics = SynchronizedLyrics.fromString(lyrics)

            return when {
                syncedLyrics != null -> LyricsResult.FoundSyncedLyrics(
                    syncedLyrics,
                    LyricsFetchSource.FROM_SONG_METADATA,
                )
                lyrics.isNotBlank() -> LyricsResult.FoundPlainLyrics(
                    PlainLyrics.fromString(lyrics),
                    LyricsFetchSource.FROM_SONG_METADATA,
                )
                else -> null
            }
        }

        private suspend fun getLyricsFromDatabase(title: String, album: String, artist: String): LyricsResult? {
            val lyricsEntity = lyricsDao.getSongLyrics(title, album, artist) ?: return null

            if (lyricsEntity.syncedLyrics.isNotBlank()) {
                val synced = SynchronizedLyrics.fromString(lyricsEntity.syncedLyrics)
                if (synced != null) {
                    return LyricsResult.FoundSyncedLyrics(synced, LyricsFetchSource.FROM_INTERNET)
                } else {
                    return LyricsResult.FoundPlainLyrics(
                        PlainLyrics.fromString(lyricsEntity.plainLyrics),
                        LyricsFetchSource.FROM_INTERNET,
                    )
                }
            }
            return null
        }

        private suspend fun getLyricsFromApi(
            title: String,
            album: String,
            artist: String,
            durationSeconds: Int,
        ): LyricsResult {
            return try {
                val lyricsNetwork = lyricsDataSource.getSongLyrics(artist, title, "", durationSeconds)
                val syncedLyrics = SynchronizedLyrics.fromString(lyricsNetwork.syncedLyrics)

                lyricsDao.saveSongLyrics(
                    LyricsEntity(
                        0,
                        title,
                        album,
                        artist,
                        lyricsNetwork.plainLyrics,
                        lyricsNetwork.syncedLyrics,
                    ),
                )

                if (syncedLyrics != null) {
                    LyricsResult.FoundSyncedLyrics(syncedLyrics, LyricsFetchSource.FROM_INTERNET)
                } else {
                    LyricsResult.FoundPlainLyrics(
                        PlainLyrics.fromString(lyricsNetwork.plainLyrics),
                        LyricsFetchSource.FROM_INTERNET,
                    )
                }
            } catch (e: NotFoundException) {
                e.printStackTrace()
                LyricsResult.NotFound
            } @Suppress("TooGenericExceptionCaught") catch (e: Exception) {
                e.printStackTrace()
                LyricsResult.NetworkError
            }
        }
    }