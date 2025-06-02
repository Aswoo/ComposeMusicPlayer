package com.sdu.composemusicplayer.domain.model

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
                audioId = -1,
                title = "",
                artist = "<unknown>",
                duration = 0L,
                albumPath = "",
                audioPath = "",
            )
    }
}
