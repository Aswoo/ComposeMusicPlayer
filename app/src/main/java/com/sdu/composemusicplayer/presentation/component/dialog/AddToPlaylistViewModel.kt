package com.sdu.composemusicplayer.presentation.component.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.composemusicplayer.core.model.playlist.PlaylistsRepository
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository,
) : ViewModel() {

    val state = playlistsRepository.playlistsWithInfoFlows.map {
        AddToPlaylistState.Success(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AddToPlaylistState.Loading)

    private val _selectedPlaylistId = MutableStateFlow<Int?>(null)
    val selectedPlaylistId: StateFlow<Int?> = _selectedPlaylistId


    fun addMusicToPlaylists(music: List<Music>, playlists: List<PlaylistInfo>) {
        playlistsRepository.addMusicToPlaylists(music.map { it.audioPath }, playlists)
    }

    fun clearAllPlaylists() {

    }

    fun onCreateNewPlaylist() {

    }

    fun selectPlayList(playListId: Int) {
        if(_selectedPlaylistId.value == playListId){
            _selectedPlaylistId.value = null
        }else {
            _selectedPlaylistId.value = playListId
        }
    }

    fun onComplete(musicUri: String) {
        if (selectedPlaylistId.value != null) {
            playlistsRepository.addMusicToPlaylist(musicUri, selectedPlayListId = selectedPlaylistId.value!!)
        }
    }

}

sealed interface AddToPlaylistState {
    data object Loading : AddToPlaylistState
    data class Success(val playlists: List<PlaylistInfo>) : AddToPlaylistState
}