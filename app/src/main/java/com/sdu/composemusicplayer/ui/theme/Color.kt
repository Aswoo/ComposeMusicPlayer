@file:Suppress("MagicNumber")
package com.sdu.composemusicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Gray200 = Color(0xEEEEEE)
val Gray500 = Color(0xFF9E9E9E)
val Gray600 = Color(0xFF757575)
val Gray700 = Color(0xFF616161)

val SpotiGreen = Color(0xFF1DB954)
val SpotiBlack = Color(0xFF616161)
val SpotiBlackBar = Color(0xFF121212)
val SpotiBackground = Color(0xFF121212)
val SpotiWhite = Color(0xFFF5F5F5)
val SpotiDarkGray = Color(0xFF282828)
val SpotiGray = Color(0xFFB3B3B3)
val SpotiDivider = Color(0xFF404040)
val SpotiLightGray = Color(0xFFB3B3B3)

val LightRed = Color(0xFFED4956)

val TextDefaultColor: Color
    @Composable get() = if (!isSystemInDarkTheme()) Color(0xFF4B4B4B) else Color(0xFFEEEEEE)
val TintDefaultColor: Color
    @Composable get() = if (!isSystemInDarkTheme()) Color(0xFF4B4B4B) else Color(0xFFEEEEEE)
