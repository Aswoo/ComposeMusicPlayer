package com.sdu.composemusicplayer.presentation.mainScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun AudioWave(isMusicPlaying: Boolean) {
    // 가장 간단하고 확실한 방법
    var fraction1 by remember { mutableFloatStateOf(0.3f) }
    var fraction2 by remember { mutableFloatStateOf(0.5f) }
    var fraction3 by remember { mutableFloatStateOf(0.4f) }

    LaunchedEffect(isMusicPlaying) {
        if (isMusicPlaying) {
            launch {
                while (isMusicPlaying) {
                    // 첫 번째 막대 애니메이션
                    repeat(1000) { step ->
                        if (!isMusicPlaying) return@launch
                        val progress = step / 1000f
                        fraction1 = 0.3f + (sin(progress * 2 * PI) * 0.35f).toFloat()
                        delay(2) // 2ms마다 업데이트 = 부드러운 애니메이션
                    }
                }
            }

            launch {
                delay(300) // 약간의 위상 차이
                while (isMusicPlaying) {
                    // 두 번째 막대 애니메이션
                    repeat(800) { step ->
                        if (!isMusicPlaying) return@launch
                        val progress = step / 800f
                        fraction2 = 0.2f + (sin(progress * 2 * PI) * 0.4f).toFloat()
                        delay(2)
                    }
                }
            }

            launch {
                delay(600) // 더 큰 위상 차이
                while (isMusicPlaying) {
                    // 세 번째 막대 애니메이션
                    repeat(1200) { step ->
                        if (!isMusicPlaying) return@launch
                        val progress = step / 1200f
                        fraction3 = 0.4f + (sin(progress * 2 * PI) * 0.3f).toFloat()
                        delay(2)
                    }
                }
            }
        } else {
            // 정지 시 원래 위치로 복귀
            launch {
                val start1 = fraction1
                val start2 = fraction2
                val start3 = fraction3

                repeat(150) { step ->
                    // 300ms 동안
                    val progress = step / 150f
                    fraction1 = start1 + (0.3f - start1) * progress
                    fraction2 = start2 + (0.5f - start2) * progress
                    fraction3 = start3 + (0.4f - start3) * progress
                    delay(2)
                }
                // 최종 값 보장
                fraction1 = 0.3f
                fraction2 = 0.5f
                fraction3 = 0.4f
            }
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier =
            Modifier
                .width(24.dp)
                .height(20.dp),
    ) {
        listOf(fraction1, fraction2, fraction3).forEach { fraction ->
            Box(
                modifier =
                    Modifier
                        .width(4.dp)
                        .fillMaxHeight(fraction.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(100))
                        .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}
