package com.sdu.composemusicplayer.presentation.playlists.playlistdetail

import com.sdu.composemusicplayer.domain.model.Music


sealed interface PlaylistDetailScreenState {

    data object Loading : PlaylistDetailScreenState

    data class Loaded(
        val id: Int,
        val name: String,
        val music: List<Music>,
    ) : PlaylistDetailScreenState

    data object Deleted : PlaylistDetailScreenState

}