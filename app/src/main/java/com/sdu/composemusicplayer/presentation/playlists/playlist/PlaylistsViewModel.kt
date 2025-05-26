package com.sdu.composemusicplayer.presentation.playlists.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.composemusicplayer.core.model.playlist.PlaylistsRepository
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.playback.PlaylistPlaybackActions
import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository,
) : ViewModel() {

    val state: StateFlow<PlaylistsScreenState> =
        playlistsRepository.playlistsWithInfoFlows
            .map {
                PlaylistsScreenState.Success(it)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, PlaylistsScreenState.Loading)

    fun onDelete(id: Int) {
        playlistsRepository.deletePlaylist(id)
    }

    fun onRename(id: Int, name: String) {
        playlistsRepository.renamePlaylist(id, name)
    }

//    override fun shufflePlaylistNext(playlistId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val songs = playlistsRepository.getPlaylistSongs(playlistId)
//            shuffleNext(songs)
//        }
//    }
//
//    override fun shufflePlaylist(playlistId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val songs = playlistsRepository.getPlaylistSongs(playlistId)
//            val shuffled = songs.shuffled()
//
//            if (shuffled.isNotEmpty()) {
//                playerEnvironment.updateQueue(shuffled)
//                playerEnvironment.play(shuffled.first())
//            }
//        }
//    }
//
//    override fun addPlaylistToQueue(playlistId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val currentQueue = playerEnvironment.observeQueue().first()
//            val songsToAdd = playlistsRepository.getPlaylistSongs(playlistId)
//            val newQueue = currentQueue + songsToAdd
//            playerEnvironment.updateQueue(newQueue)
//        }
//    }
//
//    override fun addPlaylistToNext(playlistId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val queue = playerEnvironment.observeQueue().first().toMutableList()
//            val current = playerEnvironment.getCurrentPlayedMusic().first()
//            val songsToInsert = playlistsRepository.getPlaylistSongs(playlistId)
//
//            val currentIndex = queue.indexOfFirst { it.audioPath == current.audioPath }
//            if (currentIndex != -1) {
//                queue.addAll(currentIndex + 1, songsToInsert)
//                playerEnvironment.updateQueue(queue)
//            }
//        }
//    }
//
//    override fun playPlaylist(playlistId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val songs = playlistsRepository.getPlaylistSongs(playlistId)
//            if (songs.isNotEmpty()) {
//                playerEnvironment.updateQueue(songs)
//                playerEnvironment.play(songs.first())
//            }
//        }
//    }
//
//    private fun shuffleNext(music: List<Music>) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val shuffled = music.shuffled()
//            if (shuffled.isNotEmpty()) {
//                playerEnvironment.updateQueue(shuffled)
//                playerEnvironment.play(shuffled.first())
//            }
//        }
//    }
}
