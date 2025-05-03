package com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent

import PlayingProgress
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.LiveLyricsScreen
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.fadingEdge
import com.sdu.composemusicplayer.ui.theme.TextDefaultColor
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel

@Composable
fun ExpandedMusicPlayerContent(playerVM: PlayerViewModel) {
    val musicUiState by playerVM.uiState.collectAsState()
    val context = LocalContext.current
    val screenState = remember { mutableStateOf(true) }

    if (!screenState.value) {
        val fadeBrush = remember {
            Brush.verticalGradient(
                0.0f to Color.Red,
                0.7f to Color.Red,
                1.0f to Color.Transparent,
            )
        }
        LiveLyricsScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .fadingEdge(fadeBrush)
                .padding(horizontal = 8.dp, vertical = 18.dp),
            onSwap = {
                screenState.value = !screenState.value
            },
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp)) // top_bar height

        // Album Image
        AnimatedVinyl(
            albumImagePath = musicUiState.currentPlayedMusic.albumPath,
            modifier = Modifier
                .fillMaxWidth()
                .height(256.dp),
            isPlaying = musicUiState.isPlaying
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Title & Artist
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = musicUiState.currentPlayedMusic.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextDefaultColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Text(
                text = musicUiState.currentPlayedMusic.artist,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextDefaultColor
                ),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }

        // Main Player Control
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            PlayingProgress(
                maxDuration = musicUiState.currentPlayedMusic.duration,
                currentDuration = musicUiState.currentDuration,
                onChange = { progress ->
                    val duration = progress * musicUiState.currentPlayedMusic.duration
                    playerVM.onEvent(PlayerEvent.SnapTo(duration.toLong()))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayControlButton(
                isPlaying = musicUiState.isPlaying,
                onPrevious = { playerVM.onEvent(PlayerEvent.Previous) },
                onPlayPause = { playerVM.onEvent(PlayerEvent.PlayPause(musicUiState.isPlaying)) },
                onNext = { playerVM.onEvent(PlayerEvent.Next) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    println("가사 보기 클릭 $screenState")
                    screenState.value = !screenState.value
                }
            ) {
                Text("가사 보기")
            }
        }
    }
}
