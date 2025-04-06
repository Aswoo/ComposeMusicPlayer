package com.sdu.composemusicplayer.data.lyrics

import android.content.Context
import android.net.Uri
import com.sdu.composemusicplayer.network.data.LyricsSource
import com.sdu.composemusicplayer.network.model.NotFoundException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LyricsRepository @Inject constructor(
    @ApplicationContext private val  context: Context,
    private val lyricsDataSource: LyricsSource
) {

    /**
     * Gets lyrics of some song with specific URI.
     * The song file itself is checked for any embedded lyrics,
     * if they are not found in the file, then we check the database for any cached
     * lyrics, and if they are not found in the database, we use the API service.
     */
    suspend fun getLyrics(
        uri: Uri?,
        title: String,
        album: String,
        artist: String,
        durationSeconds: Int
    ): LyricsResult = withContext(Dispatchers.IO) {

        // finally check from the API
        return@withContext try {
            val lyricsNetwork =
                lyricsDataSource.getSongLyrics(artist, title, "", durationSeconds)
            val syncedLyrics = SynchronizedLyrics.fromString(lyricsNetwork.syncedLyrics)
//            lyricsDao.saveSongLyrics(
//                LyricsEntity(
//                    0,
//                    title,
//                    album,
//                    artist,
//                    lyricsNetwork.plainLyrics,
//                    lyricsNetwork.syncedLyrics
//                )
//            )
            if (syncedLyrics != null)
                LyricsResult.FoundSyncedLyrics(
                    syncedLyrics,
                    LyricsFetchSource.FROM_INTERNET
                )
            else LyricsResult.FoundPlainLyrics(
                PlainLyrics.fromString(
                    lyricsNetwork.plainLyrics,
                ),
                LyricsFetchSource.FROM_INTERNET
            )
        } catch (e: NotFoundException) {
            LyricsResult.NotFound
        } catch (e: Exception) {
            LyricsResult.NetworkError
        }
    }

}
