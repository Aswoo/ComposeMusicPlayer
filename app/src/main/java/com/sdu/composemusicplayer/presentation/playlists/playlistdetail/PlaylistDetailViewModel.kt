package com.sdu.composemusicplayer.presentation.playlists.playlistdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.composemusicplayer.domain.repository.PlaylistsRepository
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.PlaySource
import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val playlistDao: PlaylistsRepository,
        private val playerEnvironment: IPlayerEnvironment,
    ) : ViewModel(), PlaylistActions {
        private val _state =
            MutableStateFlow<PlaylistDetailScreenState>(PlaylistDetailScreenState.Loading)
        val state: StateFlow<PlaylistDetailScreenState> get() = _state

        private var collectionJob: Job

        private val id: String =
            savedStateHandle.get<String>("id")
                ?: throw IllegalArgumentException("Playlist Id not given")

        init {

            collectionJob =
                viewModelScope.launch {
                    playlistDao.getPlaylistWithSongsFlow(id.toInt()).collect {
                        _state.emit(
                            PlaylistDetailScreenState.Loaded(
                                it.playlistInfo.id, it.playlistInfo.name, it.songs,
                            ),
                        )
                    }
                }
        }

        fun onSongClicked(song: Music) {
            val state = _state.value
            if (state !is PlaylistDetailScreenState.Loaded) return

            val songs = state.music
            val index = songs.indexOf(song)
            if (index == -1) return

            viewModelScope.launch {
                playerEnvironment.play(music = song, playSource = PlaySource.PLAYLIST, playList = songs)
                _state.emit(
                    state.copy(currentPlayingMusic = song),
                )
            }
        }

        override fun removeSongs(songUris: List<String>) {
            playlistDao.removeMusicFromPlaylist(id.toInt(), songUris)
        }

        override fun delete() {
            collectionJob.cancel()
            playlistDao.deletePlaylist(id.toInt())
            _state.value = PlaylistDetailScreenState.Deleted
        }

        override fun playNext() {
            val state = _state.value
            if (state !is PlaylistDetailScreenState.Loaded) return

            val songs = state.music
            viewModelScope.launch {
                playerEnvironment.next()
            }
        }

        override fun addToQueue() {
            val state = _state.value
            if (state !is PlaylistDetailScreenState.Loaded) return

            val songs = state.music
            viewModelScope.launch {
                playerEnvironment.next()
                // playbackManager.addToQueue(songs)
            }
        }

        override fun shuffle() {
            val state = _state.value
            if (state !is PlaylistDetailScreenState.Loaded) return

            val songs = state.music
//        playbackManager.shuffle(songs)
        }

        override fun shuffleNext() {
            val state = _state.value
            if (state !is PlaylistDetailScreenState.Loaded) return

            val songs = state.music
//        playbackManager.shuffleNext(songs)
        }

        override fun rename(newName: String) {
            playlistDao.renamePlaylist(id.toInt(), newName)
        }

        override fun play() {
            val state = _state.value
            if (state !is PlaylistDetailScreenState.Loaded) return

            val song = state.music.first()
            viewModelScope.launch {
                playerEnvironment.play(song, playSource = PlaySource.PLAYLIST, state.music)
            }
        }
    }

interface PlaylistActions {
    fun play()

    fun shuffle()

    fun playNext()

    fun shuffleNext()

    fun rename(newName: String)

    fun addToQueue()

    fun delete()

    fun removeSongs(songUris: List<String>)
}
