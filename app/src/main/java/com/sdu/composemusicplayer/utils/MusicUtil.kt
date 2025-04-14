package com.sdu.composemusicplayer.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.data.music.MusicEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

object MusicUtil {
    fun fetchMusicFromDevice(
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
    suspend fun getSongPath(context: Context,uri: Uri): String = withContext(Dispatchers.IO) {

        val projection =
            arrayOf(
                MediaStore.Audio.Media.DATA,
            )
        val selection = "${MediaStore.Audio.Media._ID} = ${uri.lastPathSegment!!}"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null,
            null
        ) ?: throw Exception("Invalid cursor")

        cursor.use {
            it.moveToFirst()
            val pathColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)
            return@withContext it.getString(pathColumn)
        }
    }
}

fun <T> Collection<T>.move(
    from: Int,
    to: Int,
): List<T> {
    if (from == to) return this.toList()
    return ArrayList(this).apply {
        val temp = get(from)
        removeAt(from)
        add(to, temp)
    }
}

fun <T> SnapshotStateList<T>.swap(newList: List<T>): SnapshotStateList<T> {
    clear()
    addAll(newList)

    return this
}
