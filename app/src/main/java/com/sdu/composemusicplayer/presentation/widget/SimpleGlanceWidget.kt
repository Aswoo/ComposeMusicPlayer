package com.sdu.composemusicplayer.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sdu.composemusicplayer.R

/**
 * 가장 기본적인 Jetpack Glance 위젯
 * 최소한의 기능만 포함합니다.
 */
class SimpleGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            Column(
                modifier =
                    GlanceModifier
                        .fillMaxSize()
                        .padding(16.dp),
            ) {
                Text(
                    text = "🎵 Compose Music Player",
                    style =
                        TextStyle(
                            color = ColorProvider(Color(0xFF1DB954)), // Spotify Green
                        ),
                )
                Text(
                    text = "음악을 재생해보세요",
                    style =
                        TextStyle(
                            color = ColorProvider(Color(0xFFB3B3B3)), // Spotify Gray
                        ),
                )
            }
        }
    }
}

/**
 * Simple Glance Widget Receiver
 */
class SimpleGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SimpleGlanceWidget()
}