@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)

package com.sdu.composemusicplayer.presentation.mainScreen.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.core.constants.AppConstants
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.ui.theme.Inter
import com.sdu.composemusicplayer.ui.theme.SpotiGreen

private val SELECTED_BACKGROUND_COLOR = Color(AppConstants.SELECTED_BACKGROUND_COLOR)
private val ITEM_SHAPE_RADIUS = 8.dp
private val HORIZONTAL_PADDING = 12.dp
private val VERTICAL_PADDING = 8.dp
private val ITEM_HEIGHT = 72.dp
private val ALBUM_ART_SIZE = 56.dp
private const val SELECTED_ARTIST_ALPHA = 0.8f
private val ARTIST_FONT_SIZE = 13.sp

@Composable
private fun AlbumArt(music: Music) {
    AsyncImage(
        model = music.albumPath.toUri(),
        contentDescription = null,
        modifier =
            Modifier
                .padding(VERTICAL_PADDING)
                .size(ALBUM_ART_SIZE)
                .clip(RoundedCornerShape(ITEM_SHAPE_RADIUS)),
        placeholder = painterResource(id = R.drawable.ic_music_unknown),
        error = painterResource(id = R.drawable.ic_music_unknown),
        fallback = painterResource(id = R.drawable.ic_music_unknown),
    )
}

@Composable
private fun RowScope.MusicDetails(
    music: Music,
    selected: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .padding(start = HORIZONTAL_PADDING)
                .weight(1f),
    ) {
        Text(
            text = music.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    color = if (selected) SpotiGreen else Color.White,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
        Text(
            text = music.artist,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style =
                MaterialTheme.typography.bodySmall.copy(
                    color = if (selected) SpotiGreen.copy(alpha = SELECTED_ARTIST_ALPHA) else Color.Gray,
                    fontFamily = Inter,
                    fontSize = ARTIST_FONT_SIZE,
                ),
        )
    }
}

@Composable
private fun PlayingIndicator(
    selected: Boolean,
    isMusicPlaying: Boolean,
) {
    Box(
        modifier =
            Modifier
                .padding(end = VERTICAL_PADDING)
                .alpha(if (selected) 1f else 0f),
    ) {
        AudioWave(isMusicPlaying = isMusicPlaying)
    }
}

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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING, vertical = VERTICAL_PADDING)
                    .height(ITEM_HEIGHT)
                    .background(
                        if (selected) SELECTED_BACKGROUND_COLOR else Color.Transparent,
                        shape = RoundedCornerShape(ITEM_SHAPE_RADIUS),
                    ),
        ) {
            AlbumArt(music)
            MusicDetails(music, selected)
            PlayingIndicator(selected, isMusicPlaying)
        }
    }
}
