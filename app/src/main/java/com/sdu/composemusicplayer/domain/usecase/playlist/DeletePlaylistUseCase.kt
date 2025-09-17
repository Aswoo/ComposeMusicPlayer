package com.sdu.composemusicplayer.domain.usecase.playlist

import com.sdu.composemusicplayer.domain.repository.PlaylistsRepository
import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor(
    private val playlistsRepository: PlaylistsRepository
) {
    operator fun invoke(id: Int) {
        playlistsRepository.deletePlaylist(id)
    }
}
