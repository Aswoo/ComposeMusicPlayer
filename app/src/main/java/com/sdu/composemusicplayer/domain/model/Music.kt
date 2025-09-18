package com.sdu.composemusicplayer.domain.model

import com.sdu.composemusicplayer.utils.AndroidConstants

data class Music(
    val audioId: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val albumPath: String,
    val audioPath: String,
) {
    companion object {
        val default =
            Music(
                audioId = AndroidConstants.Misc.DEFAULT_ID,
                title = "",
                artist = "<unknown>",
                duration = AndroidConstants.Misc.DEFAULT_DURATION,
                albumPath = "",
                audioPath = "",
            )
    }
}
