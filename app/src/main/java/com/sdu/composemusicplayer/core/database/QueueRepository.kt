
package com.sdu.composemusicplayer.core.database

import android.net.Uri
import androidx.core.net.toUri
import com.sdu.composemusicplayer.core.database.dao.QueueDao
import com.sdu.composemusicplayer.core.database.entity.QueueEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import com.sdu.composemusicplayer.domain.repository.QueueRepository as QueueRepositoryContract

@Singleton
class QueueRepositoryImpl
    @Inject
    constructor(
        private val queueDao: QueueDao,
    ) : QueueRepositoryContract {
        private val scope = CoroutineScope(Dispatchers.IO)

        override suspend fun getQueue(): List<DBQueueItem> =
            queueDao
                .getQueue()
                .map { it.toDBQueueItem() }

        override fun observeQueueUris(): Flow<List<String>> =
            queueDao
                .getQueueFlow()
                .map { it.map { queueItem -> queueItem.songUri } }

        override fun saveQueueFromDBQueueItems(songs: List<DBQueueItem>) {
            scope.launch {
                queueDao.changeQueue(songs.map { it.toQueueEntity() })
            }
        }

        private fun DBQueueItem.toQueueEntity() =
            QueueEntity(
                0,
                songUri.toString(),
                title,
                artist,
                album,
            )

        private fun QueueEntity.toDBQueueItem(): DBQueueItem {
            return DBQueueItem(
                songUri = songUri.toUri(),
                title = title,
                artist = artist.orEmpty(),
                album = albumTitle.orEmpty(),
            )
        }
    }

data class DBQueueItem(
    val songUri: Uri,
    val title: String,
    val artist: String,
    val album: String,
)
