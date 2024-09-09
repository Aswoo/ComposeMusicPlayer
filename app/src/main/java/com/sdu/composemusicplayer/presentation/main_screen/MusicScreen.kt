package com.sdu.composemusicplayer.presentation.main_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.kolee.composemusicexoplayer.ui.theme.Dimens
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import com.sdu.composemusicplayer.presentation.main_screen.component.BottomMusicPlayerHeight
import com.sdu.composemusicplayer.presentation.main_screen.component.BottomMusicPlayerImpl
import com.sdu.composemusicplayer.presentation.main_screen.component.MusicItem
import com.sdu.composemusicplayer.ui.theme.TextDefaultColor
import com.sdu.composemusicplayer.viewmodel.MusicUiState
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel

private val TAG = "MusicScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, playerVM: PlayerViewModel) {
    val context = LocalContext.current

    val musicUiState by playerVM.uiState.collectAsState()

    LaunchedEffect(key1 = musicUiState.currentPlayedMusic) {
        val isShowed = (musicUiState.currentPlayedMusic != MusicEntity.default)
        playerVM.onEvent(PlayerEvent.SetShowBottomPlayer(isShowed))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Spacer(modifier = Modifier.height(Dimens.One))
            TopAppBar(colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent
            ), title = {
                Text(
                    text = stringResource(id = R.string.music_player),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextDefaultColor
                    )
                )
            })

            MusicListContent(musicUiState = musicUiState) { music ->
                playerVM.onEvent(PlayerEvent.Play(music))

            }
        }
        BottomMusicPlayerImpl(
            navController = navController,
            musicUiState = musicUiState
        ) { isPlaying ->
            playerVM.onEvent(PlayerEvent.PlayPause(isPlaying))
        }
    }

    ComposableLifeCycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.d(TAG, "MusicScreen : ON_CREATE")
            }

            Lifecycle.Event.ON_START -> {
                Log.d(TAG, "MusicScreen : ON_START")
            }

            Lifecycle.Event.ON_RESUME -> {
                Log.d(TAG, "MusicScreen : ON_RESUME")
                playerVM.onEvent(PlayerEvent.RefreshMusicList)
            }

            Lifecycle.Event.ON_PAUSE -> {
                Log.d(TAG, "MusicScreen : ON_PAUSE")
            }

            Lifecycle.Event.ON_STOP -> {
                Log.d(TAG, "MusicScreen : ON_STOP")
            }
            Lifecycle.Event.ON_DESTROY -> {
                Log.d(TAG, "MusicScreen : ON_DESTROY")
            }
            Lifecycle.Event.ON_ANY -> {
                Log.d(TAG, "MusicScreen : ON_ANY")
            }
        }
    }
}

@Composable
fun MusicListContent(musicUiState: MusicUiState, onSelectedMusic: (music: MusicEntity) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        val currentAudioId = musicUiState.currentPlayedMusic.audioId

        itemsIndexed(musicUiState.musicList) { _, music ->
            MusicItem(
                music = music,
                selected = (music.audioId == currentAudioId),
                isMusicPlaying = musicUiState.isPlaying,
                onClick = { onSelectedMusic.invoke(music) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(BottomMusicPlayerHeight.value))
        }
    }
}


@Composable
fun ComposableLifeCycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }

    }
}
