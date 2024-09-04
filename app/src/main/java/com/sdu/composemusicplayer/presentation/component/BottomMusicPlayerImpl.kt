package com.sdu.composemusicplayer.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sdu.composemusicplayer.presentation.music_screen.MusicUiState

@Composable
fun BoxScope.BottomMusicPlayerImpl(
    musicUiState: MusicUiState,
    onPlayPlauseClicked: (isPlaying: Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = musicUiState.isBottomPlayerShow,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
    ) {
        BottomMusicPlayer(
            currentMusic = musicUiState.currentPlayedMusic,
            currentDuration = musicUiState.currentDuration,
            isPlaying = musicUiState.isPlaying,
            onClick = { /*TODO*/ }, onPlayPauseClicked = onPlayPlauseClicked
        )
    }
}