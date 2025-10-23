package com.sdu.composemusicplayer.presentation.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

/**
 * 간단한 스포티파이 스타일 Glance 위젯
 * 복잡한 Box 구조 없이 기본 컴포넌트만 사용
 */
class SimpleSpotifyGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            GlanceTheme {
                // 스포티파이 스타일 메인 컨테이너
                Row(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .cornerRadius(16.dp)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 앨범 아트 영역 (이모지로 대체)
                    Text(
                        text = "🎵",
                        style = TextStyle(
                            fontSize = 48.sp,
                            color = ColorProvider(Color(0xFF1DB954)), // Spotify Green
                        ),
                        modifier = GlanceModifier.size(64.dp),
                    )

                    Spacer(modifier = GlanceModifier.width(16.dp))

                    // 곡 정보 영역
                    Column(
                        modifier = GlanceModifier.fillMaxWidth(),
                    ) {
                        // 곡 제목 - 굵은 폰트, 흰색
                        Text(
                            text = "음악을 재생해보세요",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(Color(0xFFFFFFFF)), // White
                            ),
                        )
                        
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        
                        // 아티스트 - 회색, 작은 폰트
                        Text(
                            text = "Compose Music Player",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = ColorProvider(Color(0xFFB3B3B3)), // Spotify Gray
                            ),
                        )
                    }

                    Spacer(modifier = GlanceModifier.width(12.dp))

                    // 컨트롤 버튼 영역
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // 이전 곡 버튼
                            Text(
                                text = "⏮",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = ColorProvider(Color(0xFFFFFFFF)), // White
                                ),
                                modifier = GlanceModifier.size(40.dp),
                            )

                            Spacer(modifier = GlanceModifier.width(8.dp))

                            // 재생/일시정지 버튼
                            Text(
                                text = "▶",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    color = ColorProvider(Color(0xFF1DB954)), // Spotify Green
                                ),
                                modifier = GlanceModifier.size(48.dp),
                            )

                            Spacer(modifier = GlanceModifier.width(8.dp))

                            // 다음 곡 버튼
                            Text(
                                text = "⏭",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = ColorProvider(Color(0xFFFFFFFF)), // White
                                ),
                                modifier = GlanceModifier.size(40.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simple Spotify Glance App Widget Receiver
 */
class SimpleSpotifyGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SimpleSpotifyGlanceWidget()
}

