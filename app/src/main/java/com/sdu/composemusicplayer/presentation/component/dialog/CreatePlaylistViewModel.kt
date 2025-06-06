package com.sdu.composemusicplayer.presentation.component.dialog

import androidx.lifecycle.ViewModel
import com.sdu.composemusicplayer.core.model.playlist.PlaylistsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CreatePlaylistViewModel
    @Inject
    constructor(
        private val playlistsRepository: PlaylistsRepository,
    ) : ViewModel() {
        /**
         * The names of the available playlists
         * Used to prevent the user from creating another list with the same name
         */
        val currentPlaylists: Flow<List<String>> =
            playlistsRepository
                .playlistsWithInfoFlows
                .map { it.map { playlist -> playlist.name } }

        fun onInsertPlaylist(name: String) {
            playlistsRepository.createPlaylist(name)
        }
    }
