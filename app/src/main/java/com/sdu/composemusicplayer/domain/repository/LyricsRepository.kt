package com.sdu.composemusicplayer.domain.repository

import android.net.Uri
import com.sdu.composemusicplayer.core.database.model.LyricsResult

interface LyricsRepository {
    suspend fun getLyrics(
        uri: Uri,
        title: String,
        album: String,
        artist: String,
        durationSeconds: Int,
    ): LyricsResult
}
