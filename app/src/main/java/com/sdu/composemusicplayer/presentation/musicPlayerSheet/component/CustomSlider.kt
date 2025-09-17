
package com.sdu.composemusicplayer.presentation.musicPlayerSheet.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        colors =
            SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.DarkGray,
                inactiveTrackColor = Color.LightGray,
            ),
        thumb = {
            // 커스텀 thumb: 얇은 세로 선
            Canvas(modifier = Modifier.size(16.dp)) {
                val lineHeight = 16.dp.toPx()
                val centerX = size.width / 2f
                val startY = (size.height - lineHeight) / 2f
                val endY = startY + lineHeight
                drawLine(
                    color = Color.Black,
                    start = Offset(centerX, startY),
                    end = Offset(centerX, endY),
                    strokeWidth = 2.dp.toPx(),
                )
            }
        },
        track = { sliderPositions ->
            // 커스텀 트랙: 얇은 바

            Canvas(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp),
            ) {
                val trackHeight = 2.dp.toPx()
                val y = size.height / 2f
                // Inactive Track
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = trackHeight,
                )
                // Active Track
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(0f, y),
                    end = Offset(sliderPositions.value * size.width, y),
                    strokeWidth = trackHeight,
                )
            }
        },
    )
}
