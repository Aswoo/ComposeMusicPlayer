
package com.sdu.composemusicplayer.core.model.playlist

import androidx.room.Embedded
import androidx.room.Relation
import com.sdu.composemusicplayer.core.database.PLAYLIST_ID_COLUMN
import com.sdu.composemusicplayer.core.database.entity.PlaylistEntity
import com.sdu.composemusicplayer.core.database.entity.PlaylistsMusicEntity

data class PlaylistWithMusicUri(
    @Embedded
    val playlistEntity: PlaylistEntity,
    @Relation(
        entity = PlaylistsMusicEntity::class,
        parentColumn = PLAYLIST_ID_COLUMN,
        entityColumn = PLAYLIST_ID_COLUMN
    )
    val musicUris: List<PlaylistsMusicEntity>,
)
