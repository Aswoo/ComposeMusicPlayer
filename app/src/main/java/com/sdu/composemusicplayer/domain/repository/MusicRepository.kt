package com.sdu.composemusicplayer.domain.repository

import android.content.Context
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAllMusics(): Flow<List<MusicEntity>>

    suspend fun insertMusic(music: MusicEntity)

    suspend fun insertMusics(vararg music: MusicEntity)

    suspend fun syncMusicWithDevice(
        isTrackSmallerThan100KBSkipped: Boolean = true,
        isTrackShorterThan60SecondsSkipped: Boolean = true,
    )

    suspend fun deleteMusics(vararg music: MusicEntity, context: Context)
}


