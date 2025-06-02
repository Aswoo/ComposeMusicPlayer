package com.sdu.composemusicplayer.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongLyricsNetwork(
    @SerialName("id")
    val lyricsId: Int,
    @SerialName("plainLyrics")
    val plainLyrics: String,
    @SerialName("syncedLyrics")
    val syncedLyrics: String,
)
