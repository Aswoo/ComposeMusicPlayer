package com.sdu.composemusicplayer.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.sdu.composemusicplayer.MainActivity
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.core.media.presentation.service.MediaService

class BasicWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        android.util.Log.d("BasicWidget", "=== onUpdate 시작 ===")
        android.util.Log.d("BasicWidget", "위젯 수: ${appWidgetIds.size}")

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(
        context: Context,
        intent: android.content.Intent,
    ) {
        android.util.Log.d("BasicWidget", "onReceive: ${intent.action}")
        super.onReceive(context, intent)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        android.util.Log.d("BasicWidget", "=== updateAppWidget 시작 ===")
        android.util.Log.d("BasicWidget", "위젯 ID: $appWidgetId")

        val remoteViews = RemoteViews(context.packageName, R.layout.widget_basic)
        android.util.Log.d("BasicWidget", "RemoteViews 생성 완료")

        // PendingIntent 설정
        setupPendingIntents(context, remoteViews)
        android.util.Log.d("BasicWidget", "PendingIntent 설정 완료")

        // MediaSession을 통해 위젯 업데이트 (비동기)
        // 기본값은 MediaSession 연결 실패 시에만 설정
        updateWithMediaSession(context, remoteViews)
    }

    private fun setupPendingIntents(
        context: Context,
        remoteViews: RemoteViews,
    ) {
        // 앱 열기
        val appIntent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val appPendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        remoteViews.setOnClickPendingIntent(R.id.widget_album_art, appPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.widget_song_title, appPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.widget_artist_name, appPendingIntent)

        // 이전 곡 버튼
        val previousIntent =
            Intent(context, MediaSessionBroadcastReceiver::class.java).apply {
                action = "ACTION_PREVIOUS"
            }
        val previousPendingIntent =
            PendingIntent.getBroadcast(
                context,
                1,
                previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        remoteViews.setOnClickPendingIntent(R.id.widget_previous_button, previousPendingIntent)

        // 재생/일시정지 버튼
        val playPauseIntent =
            Intent(context, MediaSessionBroadcastReceiver::class.java).apply {
                action = "ACTION_PLAY_PAUSE"
            }
        val playPausePendingIntent =
            PendingIntent.getBroadcast(
                context,
                2,
                playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        remoteViews.setOnClickPendingIntent(R.id.widget_play_pause_button, playPausePendingIntent)

        // 다음 곡 버튼
        val nextIntent =
            Intent(context, MediaSessionBroadcastReceiver::class.java).apply {
                action = "ACTION_NEXT"
            }
        val nextPendingIntent =
            PendingIntent.getBroadcast(
                context,
                3,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        remoteViews.setOnClickPendingIntent(R.id.widget_next_button, nextPendingIntent)
    }

    private fun updateWithMediaSession(
        context: Context,
        remoteViews: RemoteViews,
    ) {
        try {
            val sessionToken =
                SessionToken(
                    context,
                    ComponentName(context, MediaService::class.java),
                )
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

            controllerFuture.addListener({
                try {
                    val controller = controllerFuture.get()
                    android.util.Log.d("BasicWidget", "MediaSession 연결 성공!")

                    val isPlaying = controller.isPlaying
                    val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
                    remoteViews.setImageViewResource(R.id.widget_play_pause_button, playPauseIcon)

                    val metadata = controller.mediaMetadata
                    if (metadata != null && metadata.title != null) {
                        android.util.Log.d("BasicWidget", "메타데이터 있음: ${metadata.title} - ${metadata.artist}")
                        updateMetadata(context, remoteViews, metadata, isPlaying)
                    } else {
                        android.util.Log.d("BasicWidget", "메타데이터 없음, 기본값 사용")
                        setDefaultMetadata(context, remoteViews)
                    }

                    controller.release()
                } catch (e: Exception) {
                    android.util.Log.e("BasicWidget", "MediaSession 연결 실패: ${e.message}")
                    setDefaultMetadata(context, remoteViews)
                }
            }, context.mainExecutor)
        } catch (e: Exception) {
            android.util.Log.e("BasicWidget", "MediaSession 토큰 생성 실패: ${e.message}")
            setDefaultMetadata(context, remoteViews)
        }
    }

    private fun updateMetadata(
        context: Context,
        remoteViews: RemoteViews,
        metadata: MediaMetadata,
        isPlaying: Boolean,
    ) {
        val title = metadata.title ?: "알 수 없는 곡"
        val artist = metadata.artist ?: "알 수 없는 아티스트"

        // 상태에 따른 문구 설정
        val statusText =
            when {
                isPlaying -> "재생 중"
                else -> "일시정지됨"
            }

        remoteViews.setTextViewText(R.id.widget_song_title, title)
        remoteViews.setTextViewText(R.id.widget_artist_name, "$artist • $statusText")

        android.util.Log.d("BasicWidget", "메타데이터 업데이트: $title - $artist ($statusText)")

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, BasicWidgetProvider::class.java)
        appWidgetManager.updateAppWidget(componentName, remoteViews)
    }

    private fun setDefaultMetadata(
        context: Context,
        remoteViews: RemoteViews,
    ) {
        remoteViews.setTextViewText(R.id.widget_song_title, "음악을 재생해보세요")
        remoteViews.setTextViewText(R.id.widget_artist_name, "Compose Music Player • 대기 중")

        android.util.Log.d("BasicWidget", "기본 메타데이터 설정")

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, BasicWidgetProvider::class.java)
        appWidgetManager.updateAppWidget(componentName, remoteViews)
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            android.util.Log.d("BasicWidget", "=== updateAllWidgets 시작 ===")
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, BasicWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

                android.util.Log.d("BasicWidget", "발견된 위젯 수: ${appWidgetIds.size}")
                android.util.Log.d("BasicWidget", "위젯 ID 목록: ${appWidgetIds.contentToString()}")

                if (appWidgetIds.isEmpty()) {
                    android.util.Log.w("BasicWidget", "❌ 등록된 위젯이 없습니다!")
                    return
                }

                // 모든 위젯을 한 번에 업데이트 (깜박임 방지)
                val remoteViews = RemoteViews(context.packageName, R.layout.widget_basic)
                val provider = BasicWidgetProvider()

                // PendingIntent 설정
                provider.setupPendingIntents(context, remoteViews)

                // MediaSession을 통해 위젯 업데이트 (비동기)
                provider.updateWithMediaSession(context, remoteViews)

                android.util.Log.d("BasicWidget", "=== 모든 위젯 업데이트 완료 ===")
            } catch (e: Exception) {
                android.util.Log.e("BasicWidget", "❌ 위젯 업데이트 실패", e)
            }
        }
    }
}
