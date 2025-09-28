package com.sdu.composemusicplayer.core.database

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.core.database.dao.MusicDao
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import com.sdu.composemusicplayer.domain.repository.MusicRepository
import com.sdu.composemusicplayer.utils.AndroidConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MusicRepositoryImpl
    @Inject
    constructor(
        private val musicDao: MusicDao,
        @ApplicationContext private val context: Context,
    ) : MusicRepository {
        override fun getAllMusics(): Flow<List<MusicEntity>> = musicDao.getAllMusices()

        override suspend fun insertMusic(music: MusicEntity) = musicDao.insert(music)

        override suspend fun insertMusics(vararg music: MusicEntity) = musicDao.insert(music.toList())

        override suspend fun syncMusicWithDevice(
            isTrackSmallerThan100KBSkipped: Boolean,
            isTrackShorterThan60SecondsSkipped: Boolean,
        ) {
            val musicList =
                fetchMusicFromDevice(
                    context = context,
                    isTrackSmallerThan100KBSkipped = isTrackSmallerThan100KBSkipped,
                    isTrackShorterThan60SecondsSkipped = isTrackShorterThan60SecondsSkipped,
                )
            musicDao.insert(musicList)
        }

        private fun fetchMusicFromDevice(
            context: Context,
            isTrackSmallerThan100KBSkipped: Boolean = true,
            isTrackShorterThan60SecondsSkipped: Boolean = true,
        ): List<MusicEntity> {
            val musicList = mutableListOf<MusicEntity>()
            val audioUriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val musicProjection =
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.SIZE,
                )

            context
                .contentResolver
                .query(
                    audioUriExternal,
                    musicProjection,
                    null,
                    null,
                    null,
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val music = createMusicEntityFromCursor(cursor, context, audioUriExternal)
                        if (shouldAddMusic(
                                music,
                                cursor,
                                isTrackSmallerThan100KBSkipped,
                                isTrackShorterThan60SecondsSkipped,
                            )
                        ) {
                            musicList.add(music)
                        }
                    }
                }
            return musicList
        }

        private fun createMusicEntityFromCursor(
            cursor: Cursor,
            context: Context,
            audioUriExternal: Uri,
        ): MusicEntity {
            val audioID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            val albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))

            val albumPath =
                Uri.withAppendedPath(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId,
                )
            val musicPath = Uri.withAppendedPath(audioUriExternal, "" + audioID)

            return MusicEntity(
                audioId = audioID,
                title = title,
                artist =
                    if (artist.equals("<unknown>", true)) {
                        context.getString(R.string.unknown)
                    } else {
                        artist
                    },
                duration = duration,
                albumPath = albumPath.toString(),
                audioPath = musicPath.toString(),
            )
        }

        private fun shouldAddMusic(
            music: MusicEntity,
            cursor: Cursor,
            isTrackSmallerThan100KBSkipped: Boolean,
            isTrackShorterThan60SecondsSkipped: Boolean,
        ): Boolean {
            val size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
            val duration = music.duration

            val durationGreaterThanMin =
                duration.milliseconds.inWholeSeconds > AndroidConstants.Media.MIN_TRACK_DURATION_SECONDS
            val sizeGreaterThanMin = (size / AndroidConstants.Media.BYTES_IN_KB) > AndroidConstants.Media.MIN_TRACK_SIZE_KB

            return when {
                isTrackSmallerThan100KBSkipped && isTrackShorterThan60SecondsSkipped ->
                    sizeGreaterThanMin && durationGreaterThanMin
                !isTrackSmallerThan100KBSkipped && isTrackShorterThan60SecondsSkipped ->
                    durationGreaterThanMin
                isTrackSmallerThan100KBSkipped && !isTrackShorterThan60SecondsSkipped ->
                    sizeGreaterThanMin
                else -> true
            }
        }

        override suspend fun deleteMusics(
            vararg music: MusicEntity,
            context: Context,
        ) {
            music.forEach { musicItem ->
                val deletedFromDevice =
                    context.contentResolver.delete(
                        Uri.parse(musicItem.audioPath),
                        null,
                        null,
                    ) > 0

                if (!deletedFromDevice) {
                    error("Failed to delete file from device: ${musicItem.audioPath}")
                }
            }

            musicDao.delete(music.toList())
        }
    }
