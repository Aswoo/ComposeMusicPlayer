package com.sdu.composemusicplayer.core.database

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.core.database.dao.MusicDao
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MusicRepository
    @Inject
    constructor(
        private val musicDao: MusicDao,
        @ApplicationContext private val context: Context,
    ) {
        fun getAllMusics(): Flow<List<MusicEntity>> = musicDao.getAllMusices()

        suspend fun insertMusic(music: MusicEntity) = musicDao.insert(music)

        suspend fun insertMusics(vararg music: MusicEntity) = musicDao.insert(*music)

        suspend fun syncMusicWithDevice(
            isTrackSmallerThan100KBSkipped: Boolean = true,
            isTrackShorterThan60SecondsSkipped: Boolean = true,
        ) {
            val musicList =
                fetchMusicFromDevice(
                    context = context,
                    isTrackSmallerThan100KBSkipped = isTrackSmallerThan100KBSkipped,
                    isTrackShorterThan60SecondsSkipped = isTrackShorterThan60SecondsSkipped,
                )
            musicDao.insert(*musicList.toTypedArray())
        }

        private fun fetchMusicFromDevice(
            context: Context,
            isTrackSmallerThan100KBSkipped: Boolean = true,
            isTrackShorterThan60SecondsSkipped: Boolean = true,
        ): List<MusicEntity> {
            val musicList = mutableListOf<MusicEntity>()

            val audioUriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val musicProjection =
                listOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.SIZE,
                )
            val cursorIndexSongId: Int
            val cursorIndexSongTitle: Int
            val cursorIndexSongArtist: Int
            val cursorIndexSongDuration: Int
            val cursorIndexSongAlbumId: Int
            val cursorIndexSongSize: Int

            val songCursor =
                context.contentResolver.query(
                    audioUriExternal,
                    musicProjection.toTypedArray(),
                    null,
                    null,
                    null,
                )
            if (songCursor != null) {
                cursorIndexSongId = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                cursorIndexSongTitle = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                cursorIndexSongArtist = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                cursorIndexSongDuration =
                    songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                cursorIndexSongAlbumId =
                    songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                cursorIndexSongSize = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                while (songCursor.moveToNext()) {
                    val audioID = songCursor.getLong(cursorIndexSongId)
                    val title = songCursor.getString(cursorIndexSongTitle)
                    val artist = songCursor.getString(cursorIndexSongArtist)
                    val duration = songCursor.getLong(cursorIndexSongDuration)
                    val albumId = songCursor.getString(cursorIndexSongAlbumId)
                    val size = songCursor.getInt(cursorIndexSongSize)

                    val albumPath =
                        Uri.withAppendedPath(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId,
                        )
                    val musicPath = Uri.withAppendedPath(audioUriExternal, "" + audioID)

                    val durationGreaterThan60Sec = duration.milliseconds.inWholeSeconds > 60
                    val sizeGreaterThan100KB = (size / 1024) > 100

                    val music =
                        MusicEntity(
                            audioId = audioID,
                            title = title,
                            artist =
                                if (artist.equals(
                                        "<unknown>",
                                        true,
                                    )
                                ) {
                                    context.getString(R.string.unknown)
                                } else {
                                    artist
                                },
                            duration = duration,
                            albumPath = albumPath.toString(),
                            audioPath = musicPath.toString(),
                        )
                    when {
                        isTrackSmallerThan100KBSkipped and isTrackShorterThan60SecondsSkipped -> {
                            if (sizeGreaterThan100KB and durationGreaterThan60Sec) musicList.add(music)
                        }

                        !isTrackSmallerThan100KBSkipped and isTrackShorterThan60SecondsSkipped -> {
                            if (durationGreaterThan60Sec) musicList.add(music)
                        }

                        isTrackSmallerThan100KBSkipped and !isTrackShorterThan60SecondsSkipped -> {
                            if (sizeGreaterThan100KB) musicList.add(music)
                        }

                        !isTrackSmallerThan100KBSkipped and !isTrackShorterThan60SecondsSkipped -> {
                            musicList.add(music)
                        }
                    }
                }
                songCursor.close()
            }
            return musicList
        }

        suspend fun deleteMusics(
            vararg music: MusicEntity,
            context: Context,
        ) {
            music.forEach { music ->
                val deletedFromDevice =
                    context.contentResolver.delete(
                        Uri.parse(music.audioPath),
                        null,
                        null,
                    ) > 0

                if (!deletedFromDevice) {
                    throw IllegalStateException("기기에서 파일 삭제 실패: ${music.audioPath}")
                }
            }

            musicDao.delete(*music)
        }
    }
