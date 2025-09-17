package com.sdu.composemusicplayer.domain.usecase.playlist

import com.sdu.composemusicplayer.domain.repository.PlaylistsRepository
import javax.inject.Inject

class AddMusicToPlaylistUseCase @Inject constructor(
    private val playlistsRepository: PlaylistsRepository
) {
    operator fun invoke(musicUri: String, playlistId: Int) {
        playlistsRepository.addMusicToPlaylist(musicUri, playlistId)
    }
}
