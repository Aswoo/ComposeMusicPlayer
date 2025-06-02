package com.sdu.composemusicplayer.presentation.mainScreen.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.sdu.composemusicplayer.viewmodel.MusicUiState

@Composable
fun BoxScope.BottomMusicPlayerImpl(
    navController: NavController,
    musicUiState: MusicUiState,
    onPlayPlauseClicked: (isPlaying: Boolean) -> Unit,
) {
//    AnimatedVisibility(
//        visible = musicUiState.isBottomPlayerShow,
//        enter = slideInVertically(initialOffsetY = { it }),
//        exit = slideOutVertically(targetOffsetY = { it }),
//        modifier =
//            Modifier
//                .navigationBarsPadding()
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter),
//    ) {
//        BottomMusicPlayer(
//            currentMusic = musicUiState.currentPlayedMusic,
//            currentDuration = musicUiState.currentDuration,
//            isPlaying = musicUiState.isPlaying,
//            onClick = { navController.navigate(Routes.Player.name) },
//            onPlayPauseClicked = onPlayPlauseClicked,
//            modifier = Modifier.testTag("BottomMusicPlayer"),
//        )
//    }
}
