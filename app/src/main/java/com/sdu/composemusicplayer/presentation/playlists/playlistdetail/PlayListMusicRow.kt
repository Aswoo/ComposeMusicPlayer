package com.sdu.composemusicplayer.presentation.playlists.playlistdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.sdu.composemusicplayer.ui.theme.SpotiDarkGray
import com.sdu.composemusicplayer.ui.theme.SpotiGray
import com.sdu.composemusicplayer.ui.theme.SpotiGreen
import com.sdu.composemusicplayer.ui.theme.SpotiWhite
import com.sdu.composemusicplayer.utils.millisToTime

@Composable
fun PlayListMusicRow(
    modifier: Modifier = Modifier,
    music: Music,
    menuOptions: List<MenuActionItem>,
    isPlaying: Boolean,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isPlaying) SpotiDarkGray else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier =
                Modifier
                    .padding(8.dp)
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
            model = music.toSongAlbumArtModel(),
            imageLoader = LocalEfficientThumbnailImageLoader.current,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            fallback = rememberVectorPainter(image = Icons.Rounded.MusicNote),
            placeholder = painterResource(id = R.drawable.ic_music_unknown),
            error = painterResource(id = R.drawable.ic_music_unknown),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
        ) {
            Text(
                text = music.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPlaying) SpotiGreen else SpotiWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${music.artist} • Album name",
                fontSize = 12.sp,
                color = SpotiGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = music.duration.millisToTime(),
            fontSize = 12.sp,
            color = SpotiGray,
            modifier = Modifier.padding(end = 8.dp),
        )

        MusicOverflowMenu(menuOptions)
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
        actions = menuOptions,
    )
}
