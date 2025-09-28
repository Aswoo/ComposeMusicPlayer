@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.composemusicplayer.presentation.musicPlayerSheet.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.ui.theme.TintDefaultColor

private const val BUTTON_ROW_WIDTH_FRACTION = 0.8f

@Composable
fun PlayControlButton(
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth(BUTTON_ROW_WIDTH_FRACTION),
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                painter = painterResource(id = R.drawable.ic_previous_filled_rounded),
                contentDescription = null,
                tint = TintDefaultColor,
            )
        }
        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            shape = MaterialTheme.shapes.large,
            onClick = onPlayPause,
            modifier = Modifier.size(64.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    painter =
                        painterResource(
                            id =
                                if (!isPlaying) {
                                    R.drawable.ic_play_filled_rounded
                                } else {
                                    R.drawable.ic_pause_filled_rounded
                                },
                        ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        IconButton(onClick = onNext) {
            Icon(
                painter = painterResource(id = R.drawable.ic_next_filled_rounded),
                contentDescription = null,
                tint = TintDefaultColor,
            )
        }
    }
}
