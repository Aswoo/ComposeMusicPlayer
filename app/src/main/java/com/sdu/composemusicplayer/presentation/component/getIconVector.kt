package com.sdu.composemusicplayer.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.TextRotationNone
import com.sdu.composemusicplayer.domain.model.PlayBackMode

fun PlayBackMode.getIconVector() = when (this) {
    PlayBackMode.REPEAT_ONE -> Icons.Rounded.RepeatOne
    PlayBackMode.REPEAT_ALL -> Icons.Rounded.Repeat
    else -> Icons.Rounded.TextRotationNone
}