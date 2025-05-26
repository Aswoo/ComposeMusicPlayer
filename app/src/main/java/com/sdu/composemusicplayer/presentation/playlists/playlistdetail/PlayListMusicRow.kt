package com.sdu.composemusicplayer.presentation.playlists.playlistdetail

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.toSongAlbumArtModel
import com.sdu.composemusicplayer.presentation.component.menu.MenuActionItem
import com.sdu.composemusicplayer.presentation.component.menu.MusicDropdownMenu
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.LocalEfficientThumbnailImageLoader
import com.sdu.composemusicplayer.utils.millisToTime


@Composable
fun PlayListMusicRow(
    modifier: Modifier,
    music: Music,
    menuOptions: List<MenuActionItem>? = null,
    songRowState: SongRowState
) {

    Row(
        modifier = modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        MusicInfoRow(
            modifier = Modifier.weight(1f),
            music = music
        )

        Box(
            Modifier
                .fillMaxHeight()
                .width(48.dp), contentAlignment = Alignment.Center
        ) {

            if (menuOptions != null) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = songRowState == SongRowState.MENU_SHOWN,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None
                ) {
                    MusicOverflowMenu(menuOptions = menuOptions)
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = songRowState == SongRowState.SELECTION_STATE_SELECTED,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)),
                exit = scaleOut()
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

        }


    }

}

enum class SongRowState {
    MENU_SHOWN, SELECTION_STATE_NOT_SELECTED, SELECTION_STATE_SELECTED, EMPTY
}

@Composable
fun MusicInfoRow(
    modifier: Modifier,
    music: Music,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(6.dp)),
            model = music.toSongAlbumArtModel(),
            imageLoader = LocalEfficientThumbnailImageLoader.current,
            contentDescription = "Cover Photo",
            contentScale = ContentScale.Crop,
            fallback = rememberVectorPainter(image = Icons.Rounded.MusicNote),
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder),
            onError = {  }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(Modifier.weight(1f)) {

            Text(
                text = music.title,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            //Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = music.artist,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Elbum name",
                    fontSize = 11.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = music.duration.millisToTime(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun MusicOverflowMenu(menuOptions: List<MenuActionItem>) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
    }
    MusicDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        actions = menuOptions
    )
}