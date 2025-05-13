package com.sdu.composemusicplayer.domain.model

data class Playlist(
    val playlistInfo: PlaylistInfo,
    val songs: List<Music>
)