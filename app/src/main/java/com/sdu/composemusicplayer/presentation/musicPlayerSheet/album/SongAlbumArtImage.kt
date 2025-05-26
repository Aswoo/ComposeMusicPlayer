package com.sdu.composemusicplayer.presentation.musicPlayerSheet.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.domain.model.SongAlbumArtModel

@Composable
fun AlbumArtImage(
    modifier: Modifier,
    songAlbumArtModel: SongAlbumArtModel,
    crossFadeDuration: Int = 300,
) {
    val context = LocalContext.current
    val imageRequest = remember(songAlbumArtModel) {
        ImageRequest.Builder(context)
            .data(songAlbumArtModel)
            .transitionFactory(CrossfadeTransition.Factory(crossFadeDuration)).build()
    }
    AsyncImage(
        modifier = modifier,
        model = imageRequest,
        contentDescription = "Artwork",
        contentScale = ContentScale.Crop,
        imageLoader = LocalInefficientThumbnailImageLoader.current,
        error = painterResource(id = R.drawable.vinyl_background),
        placeholder = ColorPainter(Color.Transparent)
    )
}