package com.sdu.composemusicplayer.domain.usecase.playlist

import com.sdu.composemusicplayer.domain.repository.PlaylistsRepository
import javax.inject.Inject

class RemoveMusicFromPlaylistUseCase @Inject constructor(
    private val playlistsRepository: PlaylistsRepository
) {
    operator fun invoke(playlistId: Int, musicUris: List<String>) {
        playlistsRepository.removeMusicFromPlaylist(playlistId, musicUris)
    }
}
