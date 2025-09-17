import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.ui.theme.LightRed
import com.sdu.composemusicplayer.ui.theme.SpotiDarkGray
import com.sdu.composemusicplayer.ui.theme.SpotiGreen
import com.sdu.composemusicplayer.ui.theme.SpotiLightGray

private val DRAG_HANDLE_VERTICAL_PADDING = 8.dp
private val DRAG_HANDLE_WIDTH = 36.dp
private val DRAG_HANDLE_HEIGHT = 4.dp
private val DRAG_HANDLE_SHAPE_RADIUS = 2.dp
private const val DRAG_HANDLE_ALPHA = 0.5f
private val BOTTOM_SHEET_HORIZONTAL_PADDING = 20.dp
private val BOTTOM_SHEET_VERTICAL_PADDING = 16.dp
private val HEADER_ICON_SIZE = 32.dp
private val HEADER_SPACING = 16.dp
private val ACTION_ITEM_SPACING = 8.dp

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = DRAG_HANDLE_VERTICAL_PADDING)
            .size(width = DRAG_HANDLE_WIDTH, height = DRAG_HANDLE_HEIGHT)
            .clip(RoundedCornerShape(DRAG_HANDLE_SHAPE_RADIUS))
            .background(SpotiLightGray.copy(alpha = DRAG_HANDLE_ALPHA))
            .fillMaxWidth(),
    )
}

@Composable
private fun PlaylistHeader(playlistName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = BOTTOM_SHEET_VERTICAL_PADDING),
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = SpotiGreen,
            modifier = Modifier.size(HEADER_ICON_SIZE),
        )
        Spacer(modifier = Modifier.width(HEADER_SPACING))
        Text(
            text = playlistName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheet(
    playlistName: String,
    onRenameClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SpotiDarkGray,
        dragHandle = { DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BOTTOM_SHEET_HORIZONTAL_PADDING, vertical = BOTTOM_SHEET_VERTICAL_PADDING),
        ) {
            PlaylistHeader(playlistName)
            Spacer(modifier = Modifier.height(DRAG_HANDLE_HEIGHT))
            PlaylistActionItem(
                icon = Icons.Default.PushPin,
                text = "플레이리스트 고정하기",
                onClick = onPinClick,
                iconTint = SpotiGreen,
                textColor = Color.White,
            )
            Spacer(modifier = Modifier.height(ACTION_ITEM_SPACING))
            PlaylistActionItem(
                icon = Icons.Default.Edit,
                text = "플레이리스트 이름변경",
                onClick = onRenameClick,
                iconTint = SpotiGreen,
                textColor = Color.White,
            )
            Spacer(modifier = Modifier.height(ACTION_ITEM_SPACING))
            PlaylistActionItem(
                icon = Icons.Default.Delete,
                text = "플레이리스트 삭제하기",
                onClick = onDeleteClick,
                iconTint = LightRed,
                textColor = LightRed,
            )
        }
    }
}

@Composable
fun PlaylistActionItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    iconTint: Color = Color.White,
    textColor: Color = Color.White,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick,
            ).padding(vertical = 10.dp, horizontal = 12.dp),
        // vertical padding 줄임
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier =
            Modifier
                .padding(end = 16.dp) // 간격 약간 줄임
                .size(28.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            fontWeight = FontWeight.Medium,
        )
    }
}