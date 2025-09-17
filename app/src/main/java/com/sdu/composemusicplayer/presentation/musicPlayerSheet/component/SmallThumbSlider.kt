package com.sdu.composemusicplayer.presentation.musicPlayerSheet.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.ui.theme.Gray600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallThumbSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Slider(
        value = value.coerceIn(0f, 1f),
        onValueChange = onValueChange,
        colors =
            SliderDefaults.colors(
                thumbColor = Gray600,
                activeTrackColor = Gray600,
                inactiveTrackColor = Gray600.copy(alpha = 0.3f),
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .pointerInput(Unit) { /* 아무것도 하지 않음으로써 상위 gesture 차단 무효화 */ },
        thumb = {
            // 작은 원형 thumb
            Canvas(modifier = Modifier.size(8.dp)) {
                // 8dp 길이의 세로선 그리기
                val lineLength = 16.dp.toPx() // 8dp를 픽셀로 변환
                val startX = size.width / 2f // 선의 시작 X 좌표 (가로 중앙)
                val startY = (size.height - lineLength) / 2f // 선의 시작 Y 좌표

                // 끝점 좌표
                val endX = startX
                val endY = startY + lineLength

                drawLine(
                    color = Color.Black,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        },
    )
}
