@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import com.sdu.composemusicplayer.ui.theme.ComposeMusicPlayerTheme
import com.sdu.composemusicplayer.ui.theme.Inter
import com.sdu.composemusicplayer.ui.theme.Purple500

@Composable
fun SheetMusicItem(
    music: MusicEntity,
    selected: Boolean,
    elevation: Dp = 0.dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(draggedElevation = elevation),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(72.dp),
        ) {
            Image(
                painter =
                    rememberAsyncImagePainter(
                        model =
                            ImageRequest.Builder(LocalContext.current).data(music.albumPath.toUri())
                                .error(R.drawable.ic_music_unknown).placeholder(R.drawable.ic_music_unknown)
                                .build(),
                    ),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium),
            )

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier =
                    Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
            ) {
                Text(
                    text = music.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = if (selected) Purple500 else LocalContentColor.current,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Inter,
                        ),
                )
                Text(
                    text = music.artist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = if (selected) Purple500.copy(alpha = 0.7f) else LocalContentColor.current,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Inter,
                        ),
                )
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
                Icon(
                    imageVector = Icons.Rounded.DragHandle,
                    contentDescription = null,
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun PreviewMusicListContent() {
    val sampleMusic =
        MusicEntity(
            audioId = 1L,
            title = "Test Song",
            artist = "Test Artist",
            duration = 180000L,
            albumPath = "/path/to/album",
            audioPath = "/path/to/test/song.mp3",
        )
    ComposeMusicPlayerTheme {
        SheetMusicItem(music = sampleMusic, selected = true, onClick = {})
    }
}
