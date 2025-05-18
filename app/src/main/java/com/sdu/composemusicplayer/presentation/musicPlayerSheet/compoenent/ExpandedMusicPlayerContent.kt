@file:OptIn(ExperimentalAnimationApi::class)

package com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent

import PlayingProgress
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.LiveLyricsScreen
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.fadingEdge
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel

@Composable
fun ExpandedMusicPlayerContent(
    playerVM: PlayerViewModel,
    modifier: Modifier = Modifier,
    openAddToPlaylistDialog : () -> Unit,
) {
    val musicUiState by playerVM.uiState.collectAsState()
    val context = LocalContext.current
    val screenState = remember { mutableStateOf(true) }

    val accentColor = Color(0xFF1DB954)

    if (!screenState.value) {
        val fadeBrush = remember {
            Brush.verticalGradient(
                0.0f to accentColor,
                0.7f to accentColor,
                1.0f to Color.Transparent,
            )
        }
        LiveLyricsScreen(
            modifier = modifier
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // Blurred album art background
        AsyncImage(
            model = musicUiState.currentPlayedMusic.albumPath,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp)
                .alpha(0.2f),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Album Cover
            Image(
                painter = rememberAsyncImagePainter(musicUiState.currentPlayedMusic.albumPath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Title & Artist
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = musicUiState.currentPlayedMusic.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f),
                )

                Text(
                    text = musicUiState.currentPlayedMusic.artist,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFB3B3B3),
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress bar
            PlayingProgress(
                maxDuration = musicUiState.currentPlayedMusic.duration,
                currentDuration = musicUiState.currentDuration,
                onChange = { progress ->
                    val duration = progress * musicUiState.currentPlayedMusic.duration
                    playerVM.onEvent(PlayerEvent.SnapTo(duration.toLong()))
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Player controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { playerVM.onEvent(PlayerEvent.Previous) }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }

                IconButton(
                    onClick = { playerVM.onEvent(PlayerEvent.PlayPause(musicUiState.isPlaying)) },
                    modifier = Modifier
                        .size(72.dp)
                        .background(accentColor, shape = CircleShape),
                ) {
                    Icon(
                        imageVector = if (musicUiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "PlayPause",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp),
                    )
                }

                IconButton(onClick = { playerVM.onEvent(PlayerEvent.Next) }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 가사 보기 + 전환 버튼을 나란히 배치
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                // 기존 "가사 보기" 버튼
                Button(
                    onClick = {
                        println("가사 보기 클릭 $screenState")
                        screenState.value = !screenState.value
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("가사 보기")
                }

                // Plus → Check 애니메이션 전환 버튼
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, Color.White, CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .size(48.dp)
                        .clickable {
                            openAddToPlaylistDialog()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    AnimatedContent(
                        targetState = screenState.value,
                        transitionSpec = { fadeIn() with fadeOut() },
                        label = "Lyrics Toggle Icon",
                    ) { isLyricsVisible ->
                        Icon(
                            imageVector = if (isLyricsVisible) Icons.Default.Add else Icons.Default.Check,
                            contentDescription = if (isLyricsVisible) "Show Lyrics" else "Hide Lyrics",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
