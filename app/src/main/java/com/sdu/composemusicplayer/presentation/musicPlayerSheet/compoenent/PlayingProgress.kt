package com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.ui.theme.Inter
import com.sdu.composemusicplayer.ui.theme.Purple200
import com.sdu.composemusicplayer.ui.theme.TextDefaultColor
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PlayingProgress(
    maxDuration: Long,
    currentDuration: Long,
    onChange: (Float) -> Unit,
) {
    // Calculate progress
    val progress =
        remember(maxDuration, currentDuration) {
            currentDuration.toFloat() / maxDuration.toFloat()
        }

    // Helper function to format time
    fun formatTime(duration: Long): String {
        val minutes = (duration.milliseconds.inWholeMinutes).toString().padStart(2, '0')
        val seconds = (duration.milliseconds.inWholeSeconds % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }

    // Format strings
    val maxDurationString = remember(maxDuration) { formatTime(maxDuration) }
    val currentDurationString = remember(currentDuration) { formatTime(currentDuration) }

    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
        Slider(
            value = progress,
            onValueChange = onChange,
            colors =
                SliderDefaults.colors(
                    thumbColor = Purple200,
                    activeTrackColor = Purple200,
                ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = currentDurationString,
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        color = TextDefaultColor,
                        fontFamily = Inter,
                    ),
            )
            Text(
                text = maxDurationString,
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        color = TextDefaultColor,
                        fontFamily = Inter,
                    ),
            )
        }
    }
}
