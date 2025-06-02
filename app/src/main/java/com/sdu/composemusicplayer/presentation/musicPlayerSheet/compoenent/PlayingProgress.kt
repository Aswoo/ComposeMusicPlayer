import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent.CustomSlider
import com.sdu.composemusicplayer.ui.theme.Inter
import com.sdu.composemusicplayer.ui.theme.TextDefaultColor
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PlayingProgress(
    maxDuration: Long,
    currentDuration: Long,
    onChange: (Float) -> Unit,
) {
    val progress = if (maxDuration == 0L) 0f else currentDuration.toFloat() / maxDuration

    fun formatTime(duration: Long): String {
        val minutes = (duration.milliseconds.inWholeMinutes).toString().padStart(2, '0')
        val seconds = (duration.milliseconds.inWholeSeconds % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }

    Column(
        modifier =
            Modifier.fillMaxWidth().pointerInput(Unit) {
                // 슬라이더 부분에 대해서만 제스처 이벤트 처리
                detectHorizontalDragGestures { change, dragAmount ->
                    // 슬라이더의 움직임을 처리, 상위 Box에 영향을 미치지 않음
                    // 슬라이더의 이동을 관리하는 로직을 여기서 작성
                }
            },
    ) {
//        Slider(
//            value = progress.coerceIn(0f, 1f),
//            onValueChange = onChange,
//            colors = SliderDefaults.colors(
//                thumbColor = Gray200,
//                activeTrackColor = Gray200,
//                inactiveTrackColor = Gray200.copy(alpha = 0.3f),
//            ),
//            modifier = Modifier.fillMaxWidth(),
//        )
//        SmallThumbSlider(
//            value = progress.coerceIn(0f, 1f),
//            onValueChange = onChange,
//        )
        CustomSlider(
            value = progress.coerceIn(0f, 1f),
            onValueChange = onChange,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatTime(currentDuration),
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        color = TextDefaultColor,
                        fontFamily = Inter,
                    ),
            )
            Text(
                text = formatTime(maxDuration),
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        color = TextDefaultColor,
                        fontFamily = Inter,
                    ),
            )
        }
    }
}
