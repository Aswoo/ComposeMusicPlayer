package com.sdu.composemusicplayer.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.sdu.composemusicplayer.domain.model.PlaySource
import com.sdu.composemusicplayer.domain.model.SortDirection
import com.sdu.composemusicplayer.domain.model.SortOption
import com.sdu.composemusicplayer.domain.model.SortState
import com.sdu.composemusicplayer.mediaPlayer.service.PlayerServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val environment: IPlayerEnvironment,
) : StatefulViewModel<MusicUiState>(MusicUiState()) {

    fun updateSort(sortType: SortState) {
        val currentSort = uiState.value.sortState.option
        val currentOrder = uiState.value.sortState.direction

        val newOrder =
            if (currentSort == sortType.option) {
                // 같은 옵션이면 정렬 방향 토글
                if (currentOrder == SortDirection.ASCENDING) SortDirection.DESCENDING else SortDirection.ASCENDING
            } else {
                // 다른 옵션이면 ASCENDING으로 초기화
                SortDirection.ASCENDING
            }

        val sortedList = when (sortType.option) {
            SortOption.TITLE -> uiState.value.musicList.sortedBy { it.title.lowercase() }
            SortOption.ARTIST -> uiState.value.musicList.sortedBy { it.artist.lowercase() }
            SortOption.ALBUM -> uiState.value.musicList.sortedBy { it.albumPath.lowercase() }
        }.let { list -> if (newOrder == SortDirection.DESCENDING) list.reversed() else list }

        println("sortedList : ${sortedList[0].title}")

        updateState {
            copy(
                musicList = sortedList,
                sortState = SortState(option = sortType.option, direction = newOrder),
            )
        }
    }


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
        viewModelScope.launch {
//                environment.observeQueue().collect { queue ->
//                    updateState { copy(queue = queue) }
//                }
        }
    }

    fun onEvent(event: PlayerEvent, playSource: PlaySource = PlaySource.SINGLE) {
        when (event) {
            is PlayerEvent.Play -> {
                viewModelScope.launch {
                    environment.play(event.music, playSource = playSource)
                }
            }

            is PlayerEvent.PlayPause -> {
                viewModelScope.launch {
                    if (event.isPlaying) {
                        environment.pause()
                    } else {
                        environment.resume()
                    }
                }
            }

            is PlayerEvent.SetShowBottomPlayer -> {
                viewModelScope.launch {
//                        environment.setShowBottomMusicPlayer(event.isShow)
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

            is PlayerEvent.AddToQueue -> {
                viewModelScope.launch {
//                        environment.addToQueue(event.music)
                }
            }

            is PlayerEvent.PlayPlaylist -> {
//                    viewModelScope.launch {
//                        environment.loadPlaylist(event.playlistId)
//                        environment.playFromPlaylist()
//                    }
            }

            else -> {
                println("Else Event")
            }
        }
    }
}
