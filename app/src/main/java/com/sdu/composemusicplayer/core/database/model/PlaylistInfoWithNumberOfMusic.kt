
package com.sdu.composemusicplayer.core.database.model

import androidx.room.Embedded
import com.sdu.composemusicplayer.core.database.entity.PlaylistEntity

data class PlaylistInfoWithNumberOfMusic(
    @Embedded
    val playlistEntity: PlaylistEntity,
    val numberOfMusic: Int
)