package com.sdu.composemusicplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.presentation.player.PlayerViewModel
import com.sdu.composemusicplayer.utils.AndroidConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberMusicAppState(
    navHostController: NavHostController,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    playerViewModel: PlayerViewModel,
    playerScreenOffset: () -> Float,
): MusicAppState {
    return remember(
        coroutineScope,
        playerScreenOffset,
        navHostController,
    ) {
        MusicAppState(
            navHostController,
            coroutineScope,
            playerViewModel,
            playerScreenOffset,
        )
    }
}

@Stable
class MusicAppState(
    val navHostController: NavHostController,
    val coroutineScope: CoroutineScope,
    val playerViewModel: PlayerViewModel,
    val playerScreenOffset: () -> Float,
) {
    val shouldShowPlayerScreen: StateFlow<Boolean> =
        playerViewModel
            .uiState
            .map {
                val hasValidTrack = it.currentPlayedMusic != Music.default
                val isActive = it.isPlaying || it.isPaused
                hasValidTrack || isActive
            }.distinctUntilChanged()
            .stateIn(
                scope = coroutineScope,
                started =
                    SharingStarted.WhileSubscribed(
                        AndroidConstants.Time.MILLIS_IN_SECOND * AndroidConstants.Misc.SUBSCRIPTION_TIMEOUT_SECONDS.toLong(),
                    ),
                initialValue = false,
            )
}
