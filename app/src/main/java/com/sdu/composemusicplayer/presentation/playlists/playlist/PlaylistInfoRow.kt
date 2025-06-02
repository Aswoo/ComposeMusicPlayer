package com.sdu.composemusicplayer.presentation.playlists.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import com.sdu.composemusicplayer.ui.theme.SpotiGray
import com.sdu.composemusicplayer.ui.theme.SpotiWhite

@Composable
fun PlaylistInfoRow(
    modifier: Modifier,
    playlistInfo: PlaylistInfo,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.PlaylistPlay,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = SpotiWhite.copy(alpha = 0.9f), // 조금 투명하게
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlistInfo.name,
                fontSize = 18.sp,
                color = SpotiWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${playlistInfo.numberOfMusic} tracks",
                fontSize = 14.sp,
                color = SpotiGray.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
