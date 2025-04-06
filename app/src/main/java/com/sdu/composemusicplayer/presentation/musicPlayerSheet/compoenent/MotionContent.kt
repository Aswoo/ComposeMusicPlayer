@file:OptIn(ExperimentalMotionApi::class)

package com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.LiveLyricsScreen
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.fadingEdge
import com.sdu.composemusicplayer.ui.theme.TextDefaultColor
import com.sdu.composemusicplayer.ui.theme.TintDefaultColor
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel

@Composable
fun MotionContent(
    playerVM: PlayerViewModel,
    fraction: Float,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val musicUiState by playerVM.uiState.collectAsState()
    val screenState = remember { mutableStateOf(true) }

    Log.d("Fraction", fraction.toString())

    val motionScene =
        remember {
            context
                .resources
                .openRawResource(R.raw.motion_scene)
                .readBytes()
                .decodeToString()
        }

    Row(modifier = Modifier.fillMaxSize()) {
        if (screenState.value) {
            MotionLayout(
                motionScene = MotionScene(content = motionScene),
                progress = fraction,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.layoutId("top_bar"))
                AnimatedVinyl(
                    albumImagePath = musicUiState.currentPlayedMusic.albumPath,
                    modifier =
                    Modifier
                        .layoutId("album_image")
                        .fillMaxWidth()
                        .aspectRatio(1f, true),
                    isPlaying = musicUiState.isPlaying,
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.layoutId("column_title_artist"),
                ) {
                    AnimatedVisibility(visible = fraction < 0.8f) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        text = musicUiState.currentPlayedMusic.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (fraction > 0.8f) TextAlign.Start else TextAlign.Center,
                        style =
                        MaterialTheme.typography.titleMedium.copy(
                            color = TextDefaultColor,
                            fontWeight = FontWeight.Bold,
                            fontSize =
                            if (fraction > 0.8f) {
                                MaterialTheme.typography.titleLarge.fontSize
                            } else {
                                MaterialTheme.typography.titleMedium.fontSize
                            },
                        ),
                        modifier =
                        Modifier.fillMaxWidth(
                            if (fraction > 0.8f) 1f else 0.7f,
                        ),
                    )
                    Text(
                        text = musicUiState.currentPlayedMusic.artist,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (fraction > 0.8f) TextAlign.Start else TextAlign.Center,
                        style =
                        MaterialTheme.typography.titleMedium.copy(
                            color = TextDefaultColor,
                            fontSize =
                            if (fraction > 0.8f) {
                                MaterialTheme.typography.titleSmall.fontSize
                            } else {
                                MaterialTheme.typography.titleMedium.fontSize
                            },
                        ),
                        modifier =
                        Modifier.fillMaxWidth(
                            if (fraction > 0.8f) 1f else 0.7f,
                        ),
                    )
                }

                Row(modifier = Modifier.layoutId("top_player_buttons")) {
                    IconButton(
                        onClick = { playerVM.onEvent(PlayerEvent.PlayPause(musicUiState.isPlaying)) },
                    ) {
                        Icon(
                            painter =
                            painterResource(
                                id =
                                if (!musicUiState.isPlaying) {
                                    R.drawable.ic_play_filled_rounded
                                } else {
                                    R.drawable.ic_pause_filled_rounded
                                },
                            ),
                            contentDescription = null,
                            tint = TintDefaultColor,
                        )
                    }

                    IconButton(onClick = { playerVM.onEvent(PlayerEvent.Next) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next_filled_rounded),
                            contentDescription = null,
                            tint = TintDefaultColor,
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.layoutId("main_player_control"),
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    PlayingProgress(
                        maxDuration = musicUiState.currentPlayedMusic.duration,
                        currentDuration = musicUiState.currentDuration,
                        onChange = { progress ->
                            val duration = progress * musicUiState.currentPlayedMusic.duration
                            playerVM.onEvent(PlayerEvent.SnapTo(duration.toLong()))
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PlayControlButton(
                        isPlaying = musicUiState.isPlaying,
                        onPrevious = {
                            playerVM.onEvent(PlayerEvent.Previous)
                        },
                        onPlayPause = {
                            playerVM.onEvent(
                                PlayerEvent.PlayPause(musicUiState.isPlaying),
                            )
                        },
                        onNext = {
                            playerVM.onEvent(PlayerEvent.Next)
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            println("가사 보기 클릭 $screenState")
                            screenState.value = !screenState.value
                        },
                    ) {
                        Text("가사 보기")
                    }
                }
            }
        } else {

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
        }

    }
}
