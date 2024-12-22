package com.sdu.composemusicplayer.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.sdu.composemusicplayer.mediaPlayer.service.PlayerServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val environment: IPlayerEnvironment,
    private val serviceManager: PlayerServiceManager
) : StatefulViewModel<MusicUiState>(MusicUiState()) {

    init {
        viewModelScope.launch {
            environment.getAllMusics().collect { musics ->
                updateState { copy(musicList = musics) }
            }
        }

        viewModelScope.launch {
            environment.getCurrentPlayedMusic().collect { music ->
                updateState { copy(currentPlayedMusic = music) }
            }
        }

        viewModelScope.launch {
            environment.isPlaying().collect { isPlaying ->
                updateState { copy(isPlaying = isPlaying) }
            }
        }

        viewModelScope.launch {
            environment.isBottomMusicPlayerShowed().collect { isShowed ->
                updateState { copy(isBottomPlayerShow = isShowed) }
            }
        }

        viewModelScope.launch {
            environment.getCurrentDuration().collect { duration ->
                updateState { copy(currentDuration = duration) }
            }
        }

        viewModelScope.launch {
            environment.isPaused().collect { isPaused ->
                updateState { copy(isPaused = isPaused) }
            }
        }
    }

    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.Play -> {
                viewModelScope.launch {
                    environment.play(event.music)
                }
                serviceManager.startMusicService()
            }

            is PlayerEvent.PlayPause -> {
                viewModelScope.launch {
                    if (event.isPlaying) {
                        environment.pause()
                    } else {
                        environment.resume()
                    }
                }
                serviceManager.startMusicService()
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
            is PlayerEvent.Previous -> {
                viewModelScope.launch {
                    environment.previous()
                }
            }

            is PlayerEvent.Next -> {
                viewModelScope.launch {
                    environment.next()
                }
            }
            is PlayerEvent.SnapTo -> {
                viewModelScope.launch {
                    environment.snapTo(event.duration)
                }
            }
            is PlayerEvent.UpdateMusicList -> {
                viewModelScope.launch {
                    environment.updateMusicList(event.musicList)
                }
            }
            is PlayerEvent.ResetIsPaused -> {
                viewModelScope.launch {
                    environment.resetIsPaused()
                }
            }
        }
    }
}
