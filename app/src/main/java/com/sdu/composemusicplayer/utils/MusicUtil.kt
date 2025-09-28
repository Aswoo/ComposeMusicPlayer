package com.sdu.composemusicplayer.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MusicUtil {
    @Suppress("TooGenericExceptionThrown")
    suspend fun getSongPath(
        context: Context,
        uri: Uri,
    ): String =
        withContext(Dispatchers.IO) {
            val projection =
                arrayOf(
                    MediaStore.Audio.Media.DATA,
                )
            val selection = "${MediaStore.Audio.Media._ID} = ${uri.lastPathSegment!!}"

            val cursor =
                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null,
                    null,
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
