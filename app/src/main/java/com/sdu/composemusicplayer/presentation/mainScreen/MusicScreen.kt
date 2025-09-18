package com.sdu.composemusicplayer.presentation.mainScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.SortOption
import com.sdu.composemusicplayer.domain.model.SortState
import com.sdu.composemusicplayer.presentation.mainScreen.component.MusicItem
import com.sdu.composemusicplayer.ui.theme.SpotiBackground
import com.sdu.composemusicplayer.presentation.player.MusicUiState
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import com.sdu.composemusicplayer.presentation.player.PlayerViewModel

private const val TAG = "MusicScreen"

@Composable
fun MainScreen(
    playerVM: PlayerViewModel,
) {
    val musicUiState by playerVM.uiState.collectAsState()

    LaunchedEffect(musicUiState.currentPlayedMusic) {
        val isShowed = (musicUiState.currentPlayedMusic != Music.default)
        playerVM.onEvent(PlayerEvent.SetShowBottomPlayer(isShowed))
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(SpotiBackground),
        // Spotify-like dark background
    ) {
        MusicListContent(
            musicUiState = musicUiState,
            onSelectedMusic = { playerVM.onEvent(PlayerEvent.Play(it)) },
            updateSortState = { sortState -> playerVM.updateSort(sortState) },
        )
    }

    ComposableLifeCycle { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            playerVM.onEvent(PlayerEvent.RefreshMusicList)
        }
        Log.d(TAG, "MusicScreen : $event")
    }
}

@Composable
fun MusicListContent(
    musicUiState: MusicUiState,
    onSelectedMusic: (Music) -> Unit,
    updateSortState: (SortState) -> Unit,
) {
    val tabTitles = listOf(SortOption.TITLE, SortOption.ARTIST)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp),
    ) {
        // 상단 타이틀

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "마이 라이브러리",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
        )

        SortHeader(
            currentSort = musicUiState.sortState,
            sortTabOption = tabTitles,
            onSortChange = {
                updateSortState(it)
            },
        )

//        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            val currentAudioId = musicUiState.currentPlayedMusic.audioId

            if (musicUiState.musicList.isEmpty()) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillParentMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "음악을 찾을 수 없습니다.",
                            color = Color.Gray,
                            fontSize = 16.sp,
                        )
                    }
                }
            } else {
                items(musicUiState.musicList, key = { music -> music.audioId }) { music ->
                    MusicItem(
                        music = music,
                        selected = (music.audioId == currentAudioId),
                        isMusicPlaying = musicUiState.isPlaying,
                        onClick = { onSelectedMusic(music) },
                    )
                }
            }
        }
    }
}

@Composable
fun ComposableLifeCycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit,
) {
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { source, event ->
                onEvent(source, event)
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
