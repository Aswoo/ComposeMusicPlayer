package com.sdu.composemusicplayer.network.model

import kotlinx.serialization.SerialName


data class SongLyricsNetwork(
    @SerialName("id")
    val lyricsId: Int,

    @SerialName("plainLyrics")
    val plainLyrics: String,

    @SerialName("syncedLyrics")
    val syncedLyrics: String
)