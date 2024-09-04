package com.sdu.composemusicplayer.presentation.music_screen

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Thread.State
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val environment: PlayerEnvironment
) :
    StatefulViewModel<MusicUiState>(MusicUiState()) {

    init {
        viewModelScope.launch {
            environment.getAllMusics().collect { musics ->
                updateState { copy(musicList = musics) }
            }
        }
        viewModelScope.launch {
            environment.isBottomMusicPlayerShowed().collect { isShowed ->
                updateState { copy(isBottomPlayerShow = isShowed) }
            }
        }
    }

    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.Play -> {
                viewModelScope.launch {
                    environment.play(event.music)
                }
            }

            is PlayerEvent.PlayPause -> {
                viewModelScope.launch {
                    if(event.isPlaying) environment.pause()
                    else environment.resume()
                }
            }

            is PlayerEvent.SetShowBottomPlayer -> {
                viewModelScope.launch {
                    environment.setShowBottomMusicPlayer(event.isShow)
                }

            }

            is PlayerEvent.RefreshMusicList -> {
                viewModelScope.launch {
                    environment.refreshMusicList()
                }
            }
        }
    }

}