
package com.sdu.composemusicplayer.domain.model

import android.net.Uri
import androidx.compose.runtime.Stable

@Stable
data class MusicAlbumArtModel(
    val albumId: Long? = null,
    val uri: Uri,
)

fun Music?.toMusicAlbumArtModel(): MusicAlbumArtModel =
    if (this == null) {
        MusicAlbumArtModel(null, Uri.EMPTY)
    } else {
        val uri = if (albumPath.isNotEmpty()) {
            try {
                Uri.parse(albumPath)
            } catch (e: Exception) {
                Uri.EMPTY
            }
        } else {
            Uri.EMPTY
        }
        MusicAlbumArtModel(albumId = audioId, uri = uri)
    }
