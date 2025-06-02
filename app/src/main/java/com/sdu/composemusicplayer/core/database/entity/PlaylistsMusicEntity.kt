package com.sdu.composemusicplayer.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sdu.composemusicplayer.core.database.MUSIC_URI_STRING_COLUMN
import com.sdu.composemusicplayer.core.database.PLAYLIST_ID_COLUMN
import com.sdu.composemusicplayer.core.database.PLAYLIST_MUSIC_ENTITY

@Entity(tableName = PLAYLIST_MUSIC_ENTITY, primaryKeys = [PLAYLIST_ID_COLUMN, MUSIC_URI_STRING_COLUMN])
data class PlaylistsMusicEntity(
    @ColumnInfo(name = PLAYLIST_ID_COLUMN)
    val playlistId: Int,
    @ColumnInfo(name = MUSIC_URI_STRING_COLUMN)
    val musicUriString: String,
)
