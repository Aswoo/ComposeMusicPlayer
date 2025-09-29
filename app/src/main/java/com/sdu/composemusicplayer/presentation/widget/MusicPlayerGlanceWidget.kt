package com.sdu.composemusicplayer.presentation.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sdu.composemusicplayer.MainActivity

/**
 * Glance로 구현한 4x1 음악 플레이어 위젯
 * XML 레이아웃과 동일한 디자인을 Compose로 구현
 */
class MusicPlayerGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            GlanceTheme {
                // 메인 컨테이너 - XML의 LinearLayout과 동일
                Row(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .cornerRadius(12.dp)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 앨범 아트 영역 - XML의 ImageView와 동일
                    Box(
                        modifier = GlanceModifier
                            .size(48.dp)
                            .appWidgetBackground()
                            .cornerRadius(8.dp),
                    ) {
                        Text(
                            text = "🎵",
                            style = TextStyle(
                                fontSize = 24.sp,
                                color = ColorProvider(Color(0xFF03DAC5)), // teal_200 색상
                            ),
                            modifier = GlanceModifier.fillMaxSize(),
                        )
                    }

                    Spacer(modifier = GlanceModifier.width(12.dp))

                    // 곡 정보 영역 - XML의 LinearLayout과 동일
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth(),
                    ) {
                        // 곡 제목 - XML의 TextView와 동일
                        Text(
                            text = "음악을 재생해보세요",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(Color(0xFFFFFFFF)), // white 색상
                            ),
                        )
                        
                        Spacer(modifier = GlanceModifier.width(2.dp))
                        
                        // 아티스트 - XML의 TextView와 동일
                        Text(
                            text = "Compose Music Player",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = ColorProvider(Color(0xFF03DAC5)), // teal_200 색상
                            ),
                        )
                    }

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    // 컨트롤 버튼 영역 - XML의 LinearLayout과 동일
                    Row {
                        // 이전 곡 버튼
                        Text(
                            text = "⏮",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = ColorProvider(Color(0xFFFFFFFF)), // white 색상
                            ),
                            modifier = GlanceModifier.size(40.dp),
                        )

                        Spacer(modifier = GlanceModifier.width(4.dp))

                        // 재생/일시정지 버튼
                        Text(
                            text = "▶",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = ColorProvider(Color(0xFFFFFFFF)), // white 색상
                            ),
                            modifier = GlanceModifier.size(40.dp),
                        )

                        Spacer(modifier = GlanceModifier.width(4.dp))

                        // 다음 곡 버튼
                        Text(
                            text = "⏭",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = ColorProvider(Color(0xFFFFFFFF)), // white 색상
                            ),
                            modifier = GlanceModifier.size(40.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Glance App Widget Receiver
 * AndroidManifest.xml에 등록됩니다.
 */
class MusicPlayerGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicPlayerGlanceWidget()
}