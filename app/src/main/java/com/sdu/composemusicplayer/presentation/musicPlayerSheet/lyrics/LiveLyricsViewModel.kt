package com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.composemusicplayer.data.lyrics.LyricsRepository
import com.sdu.composemusicplayer.data.lyrics.LyricsResult
import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import com.sdu.composemusicplayer.network.data.NetworkMonitor
import com.sdu.composemusicplayer.network.model.NetworkStatus
import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
import com.sdu.composemusicplayer.viewmodel.MusicUiState
import com.sdu.composemusicplayer.viewmodel.StatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LiveLyricsViewModel @Inject constructor(
    private val playerEnvironment: IPlayerEnvironment,
    private val lyricsRepository: LyricsRepository,
    private val networkMonitor: NetworkMonitor
) : StatefulViewModel<MusicUiState>(MusicUiState()) {

    private val _state = MutableStateFlow<LyricsScreenState>(LyricsScreenState.Loading)
    val state: StateFlow<LyricsScreenState>
        get() = _state

    init {
//        viewModelScope.launch {
//            playerEnvironment..distinctUntilChanged { old, new -> old.currentPlayingSong == new.currentPlayingSong }
//                .collect {
//                    if (it.currentPlayingSong == null) {
//                        _state.value = LyricsScreenState.NotPlaying
//                    } else {
//                        loadLyrics(it.currentPlayingSong!!)
//                    }
//                }
//        }
        viewModelScope.launch {
            playerEnvironment.getCurrentPlayedMusic().collect { music ->
                updateState { copy(currentPlayedMusic = music) }
                println("laucnch ${music.title}")
                loadLyrics(music)
            }
        }
        viewModelScope.launch {
            networkMonitor.state.collect {
                if (it == NetworkStatus.CONNECTED)
                    onRegainedNetworkConnection()
            }
        }
    }

    private fun onRegainedNetworkConnection() {
        val currentState = _state.value
        if (currentState is LyricsScreenState.NoLyrics && currentState.reason == NoLyricsReason.NETWORK_ERROR) {
            onRetry()
        }
    }

    fun onRetry() {
        viewModelScope.launch {
            playerEnvironment.getCurrentPlayedMusic().collect { music ->
                updateState { copy(currentPlayedMusic = music) }
                loadLyrics(music)
            }
        }
    }

    private suspend fun loadLyrics(song: MusicEntity) = withContext(Dispatchers.Default) {
        _state.value = LyricsScreenState.SearchingLyrics

        val cleanedArtist = song.artist.replace(Regex("\\[.*?\\]"), "").trim()
        val lyricsResult = lyricsRepository
            .getLyrics(
                null,
                song.title,
                song.albumPath,
                cleanedArtist,
                song.duration.toInt() / 1000
            )

        val newState = when (lyricsResult) {
            is LyricsResult.NotFound ->
                LyricsScreenState.NoLyrics(NoLyricsReason.NOT_FOUND)

            is LyricsResult.NetworkError ->
                LyricsScreenState.NoLyrics(NoLyricsReason.NETWORK_ERROR)

            is LyricsResult.FoundPlainLyrics ->
                LyricsScreenState.TextLyrics(lyricsResult.plainLyrics, lyricsResult.lyricsSource)

            is LyricsResult.FoundSyncedLyrics ->
                LyricsScreenState.SyncedLyrics(lyricsResult.syncedLyrics, lyricsResult.lyricsSource)
        }

        if (isActive)
            _state.value = newState
    }

    fun songProgressMillis(): Long {
        return uiState.value.currentDuration
    }

    fun setSongProgressMillis(millis: Long) {
        return playerEnvironment.snapTo(millis,true)
    }

    fun saveExternalLyricsToSongFile() {
        viewModelScope.launch {

        }
    }

}