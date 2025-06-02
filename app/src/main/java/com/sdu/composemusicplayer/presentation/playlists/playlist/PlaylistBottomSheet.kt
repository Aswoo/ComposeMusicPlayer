import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
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
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SpotiLightGray.copy(alpha = 0.5f))
                    .fillMaxWidth(),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            // Header: Playlist Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp), // 간격 줄임
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = SpotiGreen,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = playlistName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            PlaylistActionItem(
                icon = Icons.Default.PushPin,
                text = "플레이리스트 고정하기",
                onClick = onPinClick,
                iconTint = SpotiGreen,
                textColor = Color.White,
            )
            Spacer(modifier = Modifier.height(8.dp))
            PlaylistActionItem(
                icon = Icons.Default.Edit,
                text = "플레이리스트 이름변경",
                onClick = onRenameClick,
                iconTint = SpotiGreen,
                textColor = Color.White,
            )
            Spacer(modifier = Modifier.height(8.dp))
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
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick,
            )
            .padding(vertical = 10.dp, horizontal = 12.dp), // vertical padding 줄임
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier
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
