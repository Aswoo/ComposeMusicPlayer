package com.sdu.composemusicplayer.core.media

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.sdu.composemusicplayer.domain.model.Music
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import com.sdu.composemusicplayer.domain.repository.MediaRepository as MediaRepositoryContract

@file:Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.sdu.composemusicplayer.domain.model.Music
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import com.sdu.composemusicplayer.domain.repository.MediaRepository as MediaRepositoryContract

private const val TAG = "MediaRepository"

/**
 * A class that is responsible for manipulating songs on the Android device.
 * It uses the MediaStore as the underlying database and exposes all the user's
 * library inside a [StateFlow] which automatically updates when the MediaStore updates.
 * Also, it provides methods to delete songs, and change their tags.
 */
@Singleton
class MediaRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : MediaRepositoryContract {
        private var mediaSyncJob: Job? = null
        private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

        private lateinit var permissionListener: PermissionListener

        @TargetApi(29)
        override fun deleteMusic(music: Music) {
            Log.d("MediaRepository", "Deleting song $music")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                println("Attempting to delete song in R or Higher. Use Activity Contracts instead")
                return
            }

            try {
                scope.launch {
                    val musicPath = getSongPath(Uri.parse(music.audioPath))
                    val file = File(musicPath)
                    file.delete()
                    context.contentResolver.delete(Uri.parse(music.audioPath), null, null)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

        override suspend fun getSongPath(uri: Uri): String =
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

        /**
         * Called by the MainActivity to inform the repo that the user
         * granted the READ permission, in order to refresh the music library
         */
        override fun onPermissionAccepted() {
            permissionListener.onPermissionGranted()
        }

        /**
         * Interface implemented inside the callback flow of the [MediaRepository]
         * to force refresh of the song library when the user grants the permission
         */
        private fun interface PermissionListener {
            fun onPermissionGranted()
        }
    }
