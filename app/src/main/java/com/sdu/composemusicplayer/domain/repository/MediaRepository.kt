package com.sdu.composemusicplayer.domain.repository

import android.net.Uri
import com.sdu.composemusicplayer.domain.model.Music

interface MediaRepository {
    fun deleteMusic(music: Music)

    suspend fun getSongPath(uri: Uri): String

    fun onPermissionAccepted()
}


