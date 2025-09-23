package com.sdu.composemusicplayer.presentation.musicPlayerSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.twotone.Pause
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material.icons.twotone.SkipNext
import androidx.compose.material.icons.twotone.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.core.constants.AppConstants
import com.sdu.composemusicplayer.domain.model.toMusicAlbumArtModel
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.CrossFadingAlbumArt
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.ErrorPainterType
import com.sdu.composemusicplayer.ui.theme.SpotiDarkGray
import com.sdu.composemusicplayer.ui.theme.SpotiGreen
import com.sdu.composemusicplayer.ui.theme.SpotiLightGray
import com.sdu.composemusicplayer.presentation.player.MusicUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
@Suppress("LongParameterList", "LongMethod")
fun MiniPlayer(
    modifier: Modifier,
    state: MusicUiState,
    showExtraControls: Boolean,
    songProgressProvider: () -> Float,
    enabled: Boolean,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    bluetoothDeviceName: String? = null,
    onBluetoothDeviceClick: () -> Unit = {},
) {
    val song = state.currentPlayedMusic

    Row(
        modifier =
            modifier
                .background(SpotiDarkGray)
                .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CrossFadingAlbumArt(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .aspectRatio(1.0f)
                    .scale(AppConstants.DEFAULT_ALPHA)
                    .shadow(2.dp, shape = RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(8.dp)),
            containerModifier = Modifier.padding(start = 8.dp),
            songAlbumArtModel = song.toMusicAlbumArtModel(),
            errorPainterType = ErrorPainterType.PLACEHOLDER,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            // 곡 제목과 블루투스 아이콘을 같은 행에 배치
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // 블루투스 아이콘을 곡 제목 왼쪽에 배치
                if (bluetoothDeviceName != null) {
                    Icon(
                        imageVector = Icons.Filled.Bluetooth,
                        contentDescription = bluetoothDeviceName,
                        tint = SpotiGreen,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onBluetoothDeviceClick() }
                            .padding(end = 6.dp)
                    )
                }

                Text(
                    modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
            Text(
                modifier = Modifier,
                text = song.artist.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                color = SpotiLightGray,
                maxLines = 1,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row {
            AnimatedVisibility(visible = showExtraControls) {
                IconButton(onClick = onPrevious, enabled = enabled) {
                    Icon(
                        imageVector = Icons.TwoTone.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                    )
                }
            }
            Box(contentAlignment = Alignment.Center) {
                IconButton(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = onTogglePlayback,
                    enabled = enabled,
                ) {
                    val icon =
                        if (state.isPlaying) Icons.TwoTone.Pause else Icons.TwoTone.PlayArrow
                    Icon(imageVector = icon, contentDescription = null, tint = SpotiGreen)
                }
                SongCircularProgressIndicator(
                    modifier = Modifier.padding(end = 4.dp),
                    songProgressProvider = songProgressProvider,
                    progressColor = SpotiGreen,
                )
            }
            AnimatedVisibility(visible = showExtraControls) {
                IconButton(onClick = onNext, enabled = enabled) {
                    Icon(
                        imageVector = Icons.TwoTone.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
fun SongCircularProgressIndicator(
    modifier: Modifier,
    songProgressProvider: () -> Float,
    progressColor: Color = Color.White,
) {
    val progress = remember { Animatable(0.0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            val newProgress = songProgressProvider()
            progress.animateTo(newProgress)
            delay(AppConstants.LYRICS_UPDATE_INTERVAL_MS)
        }
    }

    CircularProgressIndicator(
        progress = { progress.value },
        modifier = modifier,
        strokeCap = StrokeCap.Round,
        strokeWidth = 2.dp,
        color = progressColor,
        trackColor = progressColor.copy(alpha = 0.15f),
    )
}

// 기존 enum 유지
enum class BarState {
    COLLAPSED,
    EXPANDED,
}
