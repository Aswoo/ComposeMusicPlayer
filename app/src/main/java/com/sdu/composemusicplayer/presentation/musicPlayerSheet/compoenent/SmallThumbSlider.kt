package com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.ui.theme.Gray200

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallThumbSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Slider(
        value = value.coerceIn(0f, 1f),
        onValueChange = onValueChange,
        colors = SliderDefaults.colors(
            thumbColor = Gray200,
            activeTrackColor = Gray200,
            inactiveTrackColor = Gray200.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth(),
        thumb = {
            // 작은 원형 thumb
            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = Gray200)
            }
        }
    )
}