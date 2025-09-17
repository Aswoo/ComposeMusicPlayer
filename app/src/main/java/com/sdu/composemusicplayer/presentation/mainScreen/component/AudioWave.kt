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

private const val BAR_ANIMATION_DELAY_MS = 2L
private val BAR_WIDTH = 4.dp
private val WAVE_WIDTH = 24.dp
private val WAVE_HEIGHT = 20.dp
private const val BAR_CORNER_RADIUS = 100

data class BarAnimation(
    val initialFraction: Float,
    val animationDelay: Long,
    val animationDuration: Int,
    val amplitude: Float,
    val resetDuration: Int = 150
)

@Composable
private fun animateBarFraction(
    isMusicPlaying: Boolean,
    animation: BarAnimation
): Float {
    var fraction by remember { mutableFloatStateOf(animation.initialFraction) }

    LaunchedEffect(isMusicPlaying) {
        if (isMusicPlaying) {
            launch {
                delay(animation.animationDelay)
                while (isMusicPlaying) {
                    repeat(animation.animationDuration) { step ->
                        if (!isMusicPlaying) return@launch
                        val progress = step / animation.animationDuration.toFloat()
                        fraction = animation.initialFraction + (sin(progress * 2 * PI) * animation.amplitude).toFloat()
                        delay(BAR_ANIMATION_DELAY_MS)
                    }
                }
            }
        } else {
            launch {
                val start = fraction
                repeat(animation.resetDuration) { step ->
                    val progress = step / animation.resetDuration.toFloat()
                    fraction = start + (animation.initialFraction - start) * progress
                    delay(BAR_ANIMATION_DELAY_MS)
                }
                fraction = animation.initialFraction
            }
        }
    }
    return fraction
}

@Composable
fun AudioWave(isMusicPlaying: Boolean) {
    val fraction1 = animateBarFraction(isMusicPlaying, BarAnimation(0.3f, 0, 1000, 0.35f))
    val fraction2 = animateBarFraction(isMusicPlaying, BarAnimation(0.5f, 300, 800, 0.4f))
    val fraction3 = animateBarFraction(isMusicPlaying, BarAnimation(0.4f, 600, 1200, 0.3f))

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .width(WAVE_WIDTH)
            .height(WAVE_HEIGHT),
    ) {
        listOf(fraction1, fraction2, fraction3).forEach { fraction ->
            Box(
                modifier = Modifier
                    .width(BAR_WIDTH)
                    .fillMaxHeight(fraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(BAR_CORNER_RADIUS))
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}
