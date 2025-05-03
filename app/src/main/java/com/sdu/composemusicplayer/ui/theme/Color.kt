package com.sdu.composemusicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Gray200 = Color(0xEEEEEE)

val TextDefaultColor: Color
    @Composable get() = if (!isSystemInDarkTheme()) Color(0xFF4B4B4B) else Color(0xFFEEEEEE)
val TintDefaultColor: Color
    @Composable get() = if (!isSystemInDarkTheme()) Color(0xFF4B4B4B) else Color(0xFFEEEEEE)
