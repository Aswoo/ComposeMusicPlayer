package com.sdu.composemusicplayer.domain.usecase.playlist

import com.sdu.composemusicplayer.domain.repository.PlaylistsRepository
import javax.inject.Inject

class RenamePlaylistUseCase
    @Inject
    constructor(
        private val playlistsRepository: PlaylistsRepository,
    ) {
        operator fun invoke(
            id: Int,
            newName: String,
        ) {
            playlistsRepository.renamePlaylist(id, newName)
        }
    }
