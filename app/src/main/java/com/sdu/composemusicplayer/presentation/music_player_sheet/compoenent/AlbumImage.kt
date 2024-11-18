package com.sdu.composemusicplayer.presentation.music_player_sheet.compoenent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sdu.composemusicplayer.R

@Composable
fun AlbumImage(albumPath: String, modifier: Modifier = Modifier) {
    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current).data(albumPath.toUri()).error(
                    R.drawable.ic_music_unknown
                ).placeholder(R.drawable.ic_music_unknown).build()
            ),
            contentDescription = null,
            modifier = modifier.fillMaxSize()
        )
    }
}
