
package com.sdu.composemusicplayer.domain.model

import android.net.Uri
import androidx.compose.runtime.Stable
import com.sdu.composemusicplayer.core.database.entity.MusicEntity

@Stable
data class SongAlbumArtModel(
    val albumId: Long? = null,
    val uri: Uri
)

fun Music?.toSongAlbumArtModel() = if (this == null) SongAlbumArtModel(null, Uri.EMPTY)
else SongAlbumArtModel(albumId = audioId, uri = Uri.parse(albumPath))