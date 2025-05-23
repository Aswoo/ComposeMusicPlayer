package com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.ui.theme.roundedShape

@Composable
fun AnimatedVinyl(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    albumImagePath: String,
) {
    var currentRotation by remember {
        mutableFloatStateOf(0f)
    }

    val rotation =
        remember {
            Animatable(currentRotation)
        }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
            ) {
                currentRotation = value
            }
        } else {
            if (currentRotation > 0f) {
                rotation.animateTo(
                    targetValue = currentRotation + 50,
                    animationSpec =
                        tween(
                            1250,
                            easing = LinearOutSlowInEasing,
                        ),
                ) {
                    currentRotation = value
                }
            }
        }
    }

    Vinyl(modifier = modifier, albumImagePath = albumImagePath, rotationDegrees = rotation.value)
}

@Composable
fun Vinyl(
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f,
    albumImagePath: String,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(1.0f)
                .clip(roundedShape),
    ) {
        // Vinyl background
        Image(
            modifier =
                Modifier
                    .fillMaxSize()
                    .rotate(rotationDegrees),
            painter = painterResource(id = R.drawable.vinyl_background),
            contentDescription = "Vinyl Background",
        )

        // Vinyl song cover
        Image(
            modifier =
                Modifier
                    .fillMaxSize(0.5f)
                    .rotate(rotationDegrees)
                    .aspectRatio(1.0f)
                    .align(Alignment.Center)
                    .clip(roundedShape),
            painter =
                rememberAsyncImagePainter(
                    model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(albumImagePath)
                            .placeholder(R.drawable.ic_music_unknown)
                            .error(R.drawable.ic_music_unknown)
                            .build(),
                ),
            contentDescription = "Song cover",
        )
    }
}
