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
fun PlayingProgress(maxDuration: Long, currentDuration: Long, onChange: (Float) -> Unit) {
    val progress = remember(maxDuration, currentDuration) {
        currentDuration.toFloat() / maxDuration.toFloat()
    }
    val maxDurationInMinute = remember(maxDuration) {
        maxDuration.milliseconds.inWholeMinutes
    }
    val maxDurationInSecond = remember(maxDuration) {
        maxDuration.milliseconds.inWholeSeconds % 60
    }

    val currentDurationInMinute = remember(currentDuration) {
        currentDuration.milliseconds.inWholeMinutes
    }
    val currentDurationInSecond = remember(currentDuration) {
        currentDuration.milliseconds.inWholeSeconds % 60
    }

    val maxDurationString = remember(maxDurationInMinute, maxDurationInSecond) {
        val minute =
            if (maxDurationInMinute < 10) "0$maxDurationInMinute" else maxDurationInMinute.toString()

        val second =
            if (maxDurationInSecond < 10) "0$maxDurationInSecond" else maxDurationInSecond.toString()

        return@remember "$minute:$second"
    }

    val currentDurationString = remember(currentDurationInMinute, currentDurationInSecond) {
        val minute =
            if (currentDurationInMinute < 10) "0$currentDurationInMinute" else currentDurationInMinute.toString()

        val second =
            if (currentDurationInSecond < 10) "0$currentDurationInSecond" else currentDurationInSecond.toString()

        return@remember "$minute:$second"
    }
    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
        Slider(
            value = progress,
            onValueChange = onChange,
            colors = SliderDefaults.colors(
                thumbColor = Purple200,
                activeTrackColor = Purple200
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentDurationString,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextDefaultColor,
                    fontFamily = Inter
                )
            )

            Text(
                text = maxDurationString,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextDefaultColor,
                    fontFamily = Inter
                )
            )
        }
    }
}
