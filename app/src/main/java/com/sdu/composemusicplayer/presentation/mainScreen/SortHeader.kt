package com.sdu.composemusicplayer.presentation.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdu.composemusicplayer.domain.model.SortDirection
import com.sdu.composemusicplayer.domain.model.SortOption
import com.sdu.composemusicplayer.domain.model.SortState

@Composable
fun SortHeader(
    currentSort: SortState,
    sortTabOption: List<SortOption>,
    onSortChange: (SortState) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(SortOption.TITLE) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sortTabOption.forEach { option ->
                val isSelected = selectedTab == option
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) Color.White else Color.DarkGray)
                        .clickable {
                            selectedTab = option
                            onSortChange(currentSort.copy(option = option))
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = option.name, // 또는 option.displayName (아래 참고)
                        color = if (isSelected) Color.Black else Color.LightGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

        }

        val ascending = currentSort.direction == SortDirection.ASCENDING
        val toggledDirection = if (ascending) SortDirection.DESCENDING else SortDirection.ASCENDING

        Icon(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = if (ascending) "오름차순" else "내림차순",
            modifier = Modifier
                .padding(start = 4.dp)
                .graphicsLayer {
                    scaleY = if (ascending) 1f else -1f
                }
                .clickable {
                    onSortChange(currentSort.copy(direction = toggledDirection))
                },
            tint = Color.White,
        )
    }
}
