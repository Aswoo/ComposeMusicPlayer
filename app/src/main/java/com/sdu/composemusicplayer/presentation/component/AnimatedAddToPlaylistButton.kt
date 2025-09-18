package com.sdu.composemusicplayer.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

private const val ROTATION_DEGREES = 360f

@Composable
fun AnimatedAddToPlaylistButton() {
    var isAdded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isAdded) ROTATION_DEGREES else 0f,
        animationSpec = tween(durationMillis = 500),
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (isAdded) 1f else 1f,
        animationSpec = tween(durationMillis = 300),
    )

    val icon = if (isAdded) Icons.Default.Check else Icons.Default.Add

    IconButton(onClick = { isAdded = !isAdded }) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier =
                Modifier
                    .graphicsLayer {
                        rotationZ = rotation
                    }.size(24.dp)
                    .alpha(iconAlpha),
        )
    }
}
