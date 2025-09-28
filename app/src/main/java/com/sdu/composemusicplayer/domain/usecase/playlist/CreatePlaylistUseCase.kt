package com.sdu.composemusicplayer.domain.usecase.playlist

import com.sdu.composemusicplayer.domain.repository.PlaylistsRepository
import javax.inject.Inject

class CreatePlaylistUseCase
    @Inject
    constructor(
        private val playlistsRepository: PlaylistsRepository,
    ) {
        operator fun invoke(name: String) {
            playlistsRepository.createPlaylist(name)
        }
    }
