package com.sdu.composemusicplayer.core.database

import android.content.Context
import android.net.Uri
import com.sdu.composemusicplayer.core.database.dao.LyricsDao
import com.sdu.composemusicplayer.core.database.entity.LyricsEntity
import com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource
import com.sdu.composemusicplayer.core.model.lyrics.PlainLyrics
import com.sdu.composemusicplayer.core.model.lyrics.SynchronizedLyrics
import com.sdu.composemusicplayer.core.database.model.LyricsResult
import com.sdu.composemusicplayer.network.data.LyricsSource
import com.sdu.composemusicplayer.network.model.NotFoundException
import com.sdu.composemusicplayer.utils.MusicUtil.getSongPath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File


@Singleton
class LyricsRepository @Inject constructor(
    @ApplicationContext private val  context: Context,
    private val lyricsDataSource: LyricsSource,
    private val lyricsDao: LyricsDao,
) {

    /**
     * 주어진 URI를 가진 노래의 가사를 가져옵니다.
     *
     * 순서대로 아래의 방법으로 가사를 탐색합니다:
     * 1. 오디오 파일의 메타데이터 태그(Tag)에서 임베디드된 가사를 먼저 확인합니다.
     * 2. 태그에 가사가 없을 경우, 로컬 DB에서 캐싱된 가사를 찾습니다.
     * 3. DB에도 없으면, 마지막으로 외부 API 서비스를 호출하여 가사를 가져옵니다.
     */
    suspend fun getLyrics(
        uri: Uri,
        title: String,
        album: String,
        artist: String,
        durationSeconds: Int
    ): LyricsResult = withContext(Dispatchers.IO) {

        val audioFileIO = AudioFileIO().readFile(File(getSongPath(context = context,uri)))
        val tags = audioFileIO.tagOrCreateAndSetDefault

        // check for embedded lyrics first
        kotlin.run {
            val lyrics = tags.getFirstField(FieldKey.LYRICS)?.toString() ?: return@run
            val syncedLyrics = SynchronizedLyrics.fromString(lyrics)
            if (syncedLyrics != null)
                return@withContext LyricsResult.FoundSyncedLyrics(
                    syncedLyrics,
                    LyricsFetchSource.FROM_SONG_METADATA
                )
            else if (lyrics.isNotBlank())
                return@withContext LyricsResult.FoundPlainLyrics(
                    PlainLyrics.fromString(lyrics),
                    LyricsFetchSource.FROM_SONG_METADATA
                )
        }
        // check in the DB
        kotlin.run {
            val lyricsEntity = lyricsDao.getSongLyrics(title, album, artist) ?: return@run

            if (lyricsEntity.syncedLyrics.isNotBlank()) {
                val synced = SynchronizedLyrics.fromString(lyricsEntity.syncedLyrics)
                if (synced != null) {
                    return@withContext LyricsResult.FoundSyncedLyrics(
                        synced,
                        LyricsFetchSource.FROM_INTERNET
                    )
                } else {
                    return@withContext LyricsResult.FoundPlainLyrics(
                        PlainLyrics.fromString(lyricsEntity.plainLyrics),
                        LyricsFetchSource.FROM_INTERNET
                    )
                }
            }
        }
        // finally check from the API
        return@withContext try {
            val lyricsNetwork =
                lyricsDataSource.getSongLyrics(artist, title, "", durationSeconds)
            val syncedLyrics = SynchronizedLyrics.fromString(lyricsNetwork.syncedLyrics)
            lyricsDao.saveSongLyrics(
                LyricsEntity(
                    0,
                    title,
                    album,
                    artist,
                    lyricsNetwork.plainLyrics,
                    lyricsNetwork.syncedLyrics
                )
            )
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
