package com.sdu.composemusicplayer.presentation.component.dialog

import androidx.lifecycle.ViewModel
import com.sdu.composemusicplayer.domain.repository.PlaylistsRepository
import com.sdu.composemusicplayer.domain.usecase.playlist.CreatePlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CreatePlaylistViewModel
    @Inject
    constructor(
        private val playlistsRepository: PlaylistsRepository,
        private val createPlaylistUseCase: CreatePlaylistUseCase,
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
            createPlaylistUseCase(name)
        }
    }
