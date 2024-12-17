package com.sdu.composemusicplayer.presentation.main_screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import com.sdu.composemusicplayer.navigation.Routes
import com.sdu.composemusicplayer.viewmodel.MusicUiState

@Composable
fun BoxScope.BottomMusicPlayerImpl(
    navController: NavController,
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
            onClick = { navController.navigate(Routes.Player.name) },
            onPlayPauseClicked = onPlayPlauseClicked,
            modifier = Modifier.testTag("BottomMusicPlayer")  // testTag 추가
        )
    }
}
