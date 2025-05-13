package com.sdu.composemusicplayer.presentation.playlists.playlist

import com.sdu.composemusicplayer.domain.model.PlaylistInfo


sealed interface PlaylistsScreenState {

    data object Loading : PlaylistsScreenState
    data class Success(val playlists: List<PlaylistInfo>) : PlaylistsScreenState

}