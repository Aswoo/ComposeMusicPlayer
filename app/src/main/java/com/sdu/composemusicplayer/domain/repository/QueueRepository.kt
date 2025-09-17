package com.sdu.composemusicplayer.domain.repository

import com.sdu.composemusicplayer.core.database.DBQueueItem
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    suspend fun getQueue(): List<DBQueueItem>

    fun observeQueueUris(): Flow<List<String>>

    fun saveQueueFromDBQueueItems(songs: List<DBQueueItem>)
}


