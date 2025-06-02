@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)

package com.sdu.composemusicplayer.presentation.mainScreen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.ui.theme.Inter
import com.sdu.composemusicplayer.ui.theme.SpotiGreen

@Composable
fun MusicItem(
    music: Music,
    isMusicPlaying: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
) {

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .height(72.dp)
                .background(
                    if (selected) Color(0xFF2A2A2A) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            // 앨범 이미지
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(music.albumPath.toUri())
                        .error(R.drawable.ic_music_unknown)
                        .placeholder(R.drawable.ic_music_unknown)
                        .build(),
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // 제목 & 아티스트
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = music.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (selected) SpotiGreen else Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = music.artist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (selected) SpotiGreen.copy(alpha = 0.8f) else Color.Gray,
                        fontFamily = Inter,
                        fontSize = 13.sp
                    )
                )
            }

            // 음악 재생 표시
            AnimatedVisibility(
                visible = selected,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                AudioWave(isMusicPlaying = isMusicPlaying)
            }
        }
    }
}
