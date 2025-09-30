package com.sdu.composemusicplayer.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.sdu.composemusicplayer.MainActivity
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.core.media.presentation.service.MediaService
import com.sdu.composemusicplayer.utils.AndroidConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL

/**
 * MediaSession과 연동된 음악 플레이어 위젯 Provider
 * 실제 음악 재생 상태를 위젯에 반영합니다.
 */
class MusicPlayerWidgetProvider : AppWidgetProvider() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // 성능 최적화: 마지막 업데이트 시간과 메타데이터 해시 캐시
    private var lastUpdateTime = 0L
    private var lastMetadataHash = ""

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PLAY_PAUSE -> handlePlayPause(context)
            ACTION_PREVIOUS -> handlePrevious(context)
            ACTION_NEXT -> handleNext(context)
            ACTION_OPEN_APP -> handleOpenApp(context)
        }
    }

    private fun handlePlayPause(context: Context) {
        android.util.Log.d("WidgetProvider", "재생/일시정지 버튼 클릭됨")
        val intent = Intent(context, MediaService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        context.startService(intent)
    }

    private fun handlePrevious(context: Context) {
        android.util.Log.d("WidgetProvider", "이전곡 버튼 클릭됨")
        val intent = Intent(context, MediaService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        context.startService(intent)
    }

    private fun handleNext(context: Context) {
        android.util.Log.d("WidgetProvider", "다음곡 버튼 클릭됨")
        val intent = Intent(context, MediaService::class.java).apply {
            action = ACTION_NEXT
        }
        context.startService(intent)
    }

    private fun handleOpenApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            android.util.Log.d("WidgetProvider", "=== updateAppWidget 시작 ===")
            android.util.Log.d("WidgetProvider", "위젯 ID: $appWidgetId")
            
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_music_player_simple)
            android.util.Log.d("WidgetProvider", "RemoteViews 생성 완료")

            // PendingIntent 설정
            setupPendingIntents(context, remoteViews)
            android.util.Log.d("WidgetProvider", "PendingIntent 설정 완료")

            // 먼저 기본값으로 설정
            android.util.Log.d("WidgetProvider", "기본 메타데이터 설정 중...")
            setDefaultMetadata(context, remoteViews)
            remoteViews.setImageViewResource(R.id.widget_play_pause_button, R.drawable.ic_play_arrow)
            remoteViews.setViewVisibility(R.id.widget_play_indicator, android.view.View.GONE)
            android.util.Log.d("WidgetProvider", "기본 메타데이터 설정 완료")
            
            // 위젯 즉시 업데이트
            android.util.Log.d("WidgetProvider", "위젯 즉시 업데이트 실행")
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            android.util.Log.d("WidgetProvider", "위젯 즉시 업데이트 완료")

            // MediaSession을 통해 위젯 업데이트 (비동기)
            android.util.Log.d("WidgetProvider", "MediaSession 업데이트 시작")
            updateWithMediaSession(context, remoteViews)
        }

    private fun setupPendingIntents(context: Context, remoteViews: RemoteViews) {
        // 앱 열기
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            AndroidConstants.Widget.OPEN_APP_REQUEST_CODE,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.widget_album_art, openAppPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.widget_song_title, openAppPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.widget_artist_name, openAppPendingIntent)

        // 재생/일시정지
        val playPauseIntent = Intent(context, MusicPlayerWidgetProvider::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getBroadcast(
            context,
            AndroidConstants.Widget.PLAY_PAUSE_REQUEST_CODE,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.widget_play_pause_button, playPausePendingIntent)

        // 이전 곡
        val previousIntent = Intent(context, MusicPlayerWidgetProvider::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getBroadcast(
            context,
            AndroidConstants.Widget.PREVIOUS_REQUEST_CODE,
            previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.widget_previous_button, previousPendingIntent)

        // 다음 곡
        val nextIntent = Intent(context, MusicPlayerWidgetProvider::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            context,
            AndroidConstants.Widget.NEXT_REQUEST_CODE,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.widget_next_button, nextPendingIntent)
    }

    private fun updateWithMediaSession(context: Context, remoteViews: RemoteViews) {
        try {
            // MediaSession 토큰 생성
            val sessionToken = SessionToken(
                context,
                ComponentName(context, MediaService::class.java)
            )

            // MediaController를 비동기로 연결
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

            // 디버깅: MediaSession 연결 시도 로그
            android.util.Log.d("WidgetProvider", "MediaSession 연결 시도 중...")

            controllerFuture.addListener({
                try {
                    val controller = controllerFuture.get()
                    android.util.Log.d("WidgetProvider", "MediaSession 연결 성공!")

                    // 재생 상태 확인
                    val isPlaying = controller.isPlaying
                    android.util.Log.d("WidgetProvider", "재생 상태: $isPlaying")

                    // 재생/일시정지 버튼 아이콘 설정
                    val playPauseIcon = if (isPlaying) {
                        R.drawable.ic_pause
                    } else {
                        R.drawable.ic_play_arrow
                    }
                    remoteViews.setImageViewResource(R.id.widget_btn_play_pause, playPauseIcon)
                    
                    // 재생 상태 표시 업데이트
                    remoteViews.setViewVisibility(R.id.widget_play_indicator, if (isPlaying) android.view.View.VISIBLE else android.view.View.GONE)

                    // 미디어 메타데이터 업데이트
                    val metadata = controller.mediaMetadata
                    if (metadata != null && metadata.title != null) {
                        android.util.Log.d("WidgetProvider", "메타데이터 있음: ${metadata.title} - ${metadata.artist}")
                        updateMetadata(context, remoteViews, metadata)
                    } else {
                        android.util.Log.d("WidgetProvider", "메타데이터 없음 또는 제목 없음, 기본값 사용")
                        setDefaultMetadata(context, remoteViews)
                    }

                    controller.release()
                } catch (e: Exception) {
                    android.util.Log.e("WidgetProvider", "MediaSession 연결 실패: ${e.message}")
                    // MediaSession 연결 실패 시 기본값 사용
                    setDefaultMetadata(context, remoteViews)
                    remoteViews.setImageViewResource(R.id.widget_play_pause_button, R.drawable.ic_play_arrow)
                }
            }, context.mainExecutor)

        } catch (e: Exception) {
            // MediaSession이 없을 때 기본값 사용
            setDefaultMetadata(context, remoteViews)
            remoteViews.setImageViewResource(R.id.widget_play_pause_button, R.drawable.ic_play_arrow)
        }
    }

    private fun updateMetadata(context: Context, remoteViews: RemoteViews, metadata: androidx.media3.common.MediaMetadata) {
        // 성능 최적화: 중복 업데이트 방지
        val currentTime = System.currentTimeMillis()
        val currentMetadataHash = "${metadata.title}|${metadata.artist}"
        
        // 1초 이내 중복 업데이트 방지
        if (currentTime - lastUpdateTime < 1000L && currentMetadataHash == lastMetadataHash) {
            android.util.Log.d("WidgetProvider", "중복 업데이트 방지")
            return
        }
        
        // 곡 제목
        val title = metadata.title?.toString() ?: "알 수 없는 곡"
        remoteViews.setTextViewText(R.id.widget_song_title, title)
        android.util.Log.d("WidgetProvider", "위젯 제목 설정: $title")

        // 아티스트
        val artist = metadata.artist?.toString() ?: "알 수 없는 아티스트"
        remoteViews.setTextViewText(R.id.widget_artist_name, artist)
        android.util.Log.d("WidgetProvider", "위젯 아티스트 설정: $artist")

        // 실제 위젯 업데이트
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MusicPlayerWidgetProvider::class.java)
        appWidgetManager.updateAppWidget(componentName, remoteViews)
        android.util.Log.d("WidgetProvider", "위젯 실제 업데이트 완료")

        // 상태 저장
        lastUpdateTime = currentTime
        lastMetadataHash = currentMetadataHash

        // 앨범 아트는 나중에 구현
        // TODO: 앨범 아트 이미지 로딩 구현
    }

    private fun setDefaultMetadata(context: Context, remoteViews: RemoteViews) {
        remoteViews.setTextViewText(R.id.widget_song_title, "음악을 재생해보세요")
        remoteViews.setTextViewText(R.id.widget_artist_name, "Compose Music Player")
        
        // 재생 상태 표시 숨기기
        remoteViews.setViewVisibility(R.id.widget_play_indicator, android.view.View.GONE)
        
        // 실제 위젯 업데이트
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MusicPlayerWidgetProvider::class.java)
        appWidgetManager.updateAppWidget(componentName, remoteViews)
        android.util.Log.d("WidgetProvider", "기본값으로 위젯 업데이트 완료")
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.sdu.composemusicplayer.action.PLAY_PAUSE"
        const val ACTION_PREVIOUS = "com.sdu.composemusicplayer.action.PREVIOUS"
        const val ACTION_NEXT = "com.sdu.composemusicplayer.action.NEXT"
        const val ACTION_OPEN_APP = "com.sdu.composemusicplayer.action.OPEN_APP"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, MusicPlayerWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            appWidgetIds.forEach { appWidgetId ->
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_music_player_simple)

            // PendingIntent 설정
            setupPendingIntents(context, remoteViews)

            // MediaSession을 통해 위젯 업데이트
            updateWithMediaSession(context, remoteViews)

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        private fun setupPendingIntents(context: Context, remoteViews: RemoteViews) {
            // 앱 열기
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                AndroidConstants.Widget.OPEN_APP_REQUEST_CODE,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            remoteViews.setOnClickPendingIntent(R.id.widget_album_art, openAppPendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.widget_song_title, openAppPendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.widget_artist_name, openAppPendingIntent)

            // 재생/일시정지
            val playPauseIntent = Intent(context, MusicPlayerWidgetProvider::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            val playPausePendingIntent = PendingIntent.getBroadcast(
                context,
                AndroidConstants.Widget.PLAY_PAUSE_REQUEST_CODE,
                playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            remoteViews.setOnClickPendingIntent(R.id.widget_play_pause_button, playPausePendingIntent)

            // 이전 곡
            val previousIntent = Intent(context, MusicPlayerWidgetProvider::class.java).apply {
                action = ACTION_PREVIOUS
            }
            val previousPendingIntent = PendingIntent.getBroadcast(
                context,
                AndroidConstants.Widget.PREVIOUS_REQUEST_CODE,
                previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            remoteViews.setOnClickPendingIntent(R.id.widget_previous_button, previousPendingIntent)

            // 다음 곡
            val nextIntent = Intent(context, MusicPlayerWidgetProvider::class.java).apply {
                action = ACTION_NEXT
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context,
                AndroidConstants.Widget.NEXT_REQUEST_CODE,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            remoteViews.setOnClickPendingIntent(R.id.widget_next_button, nextPendingIntent)
        }

        private fun updateWithMediaSession(context: Context, remoteViews: RemoteViews) {
            try {
                // MediaSession 토큰 생성
                val sessionToken = SessionToken(
                    context,
                    ComponentName(context, MediaService::class.java)
                )

                // MediaController를 비동기로 연결
                val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

                // 디버깅: MediaSession 연결 시도 로그
                android.util.Log.d("WidgetProvider", "MediaSession 연결 시도 중...")

                controllerFuture.addListener({
                    try {
                        val controller = controllerFuture.get()
                        android.util.Log.d("WidgetProvider", "MediaSession 연결 성공!")

                        // 재생 상태 확인
                        val isPlaying = controller.isPlaying
                        android.util.Log.d("WidgetProvider", "재생 상태: $isPlaying")

                        // 재생/일시정지 버튼 아이콘 설정
                        val playPauseIcon = if (isPlaying) {
                            R.drawable.ic_pause
                        } else {
                            R.drawable.ic_play_arrow
                        }
                        remoteViews.setImageViewResource(R.id.widget_play_pause_button, playPauseIcon)
                        
                        // 재생 상태 표시 업데이트
                        remoteViews.setViewVisibility(R.id.widget_play_indicator, if (isPlaying) android.view.View.VISIBLE else android.view.View.GONE)

                        // 미디어 메타데이터 업데이트
                        val metadata = controller.mediaMetadata
                        android.util.Log.d("WidgetProvider", "=== 메타데이터 디버깅 ===")
                        android.util.Log.d("WidgetProvider", "metadata: $metadata")
                        android.util.Log.d("WidgetProvider", "metadata.title: ${metadata?.title}")
                        android.util.Log.d("WidgetProvider", "metadata.artist: ${metadata?.artist}")
                        android.util.Log.d("WidgetProvider", "metadata.albumTitle: ${metadata?.albumTitle}")
                        
                        if (metadata != null && metadata.title != null) {
                            android.util.Log.d("WidgetProvider", "✅ 메타데이터 있음: ${metadata.title} - ${metadata.artist}")
                            updateMetadata(context, remoteViews, metadata)
                        } else {
                            android.util.Log.d("WidgetProvider", "❌ 메타데이터 없음 또는 제목 없음, 기본값 사용")
                            setDefaultMetadata(context, remoteViews)
                        }

                        // 위젯 다시 업데이트
                        val appWidgetManager = AppWidgetManager.getInstance(context)
                        val componentName = ComponentName(context, MusicPlayerWidgetProvider::class.java)
                        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                        android.util.Log.d("WidgetProvider", "위젯 재업데이트 시작 - 위젯 수: ${appWidgetIds.size}")
                        for (widgetId in appWidgetIds) {
                            android.util.Log.d("WidgetProvider", "위젯 $widgetId 재업데이트 실행")
                            appWidgetManager.updateAppWidget(widgetId, remoteViews)
                        }
                        android.util.Log.d("WidgetProvider", "위젯 재업데이트 완료")

                        controller.release()
                    } catch (e: Exception) {
                        android.util.Log.e("WidgetProvider", "MediaSession 연결 실패: ${e.message}")
                        // MediaSession 연결 실패 시 기본값 사용
                        setDefaultMetadata(context, remoteViews)
                        remoteViews.setImageViewResource(R.id.widget_play_pause_button, R.drawable.ic_play_arrow)
                    }
                }, context.mainExecutor)

            } catch (e: Exception) {
                android.util.Log.e("WidgetProvider", "MediaSession 토큰 생성 실패: ${e.message}")
                // MediaSession이 없을 때 기본값 사용
                setDefaultMetadata(context, remoteViews)
                remoteViews.setImageViewResource(R.id.widget_play_pause_button, R.drawable.ic_play_arrow)
            }
        }

        private fun updateMetadata(context: Context, remoteViews: RemoteViews, metadata: androidx.media3.common.MediaMetadata) {
            // 곡 제목
            val title = metadata.title?.toString() ?: "알 수 없는 곡"
            remoteViews.setTextViewText(R.id.widget_song_title, title)
            android.util.Log.d("WidgetProvider", "위젯 제목 설정: $title")

            // 아티스트
            val artist = metadata.artist?.toString() ?: "알 수 없는 아티스트"
            remoteViews.setTextViewText(R.id.widget_artist_name, artist)
            android.util.Log.d("WidgetProvider", "위젯 아티스트 설정: $artist")

            // 실제 위젯 업데이트
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, MusicPlayerWidgetProvider::class.java)
            appWidgetManager.updateAppWidget(componentName, remoteViews)
            android.util.Log.d("WidgetProvider", "위젯 실제 업데이트 완료")

            // 앨범 아트는 나중에 구현
            // TODO: 앨범 아트 이미지 로딩 구현
        }

        private fun setDefaultMetadata(context: Context, remoteViews: RemoteViews) {
            android.util.Log.d("WidgetProvider", "=== setDefaultMetadata 시작 ===")
            
            android.util.Log.d("WidgetProvider", "기본 제목 설정: '음악을 재생해보세요'")
            remoteViews.setTextViewText(R.id.widget_song_title, "음악을 재생해보세요")
            
            android.util.Log.d("WidgetProvider", "기본 아티스트 설정: 'Compose Music Player'")
            remoteViews.setTextViewText(R.id.widget_artist_name, "Compose Music Player")
            
            // 재생 상태 표시 숨기기
            android.util.Log.d("WidgetProvider", "재생 인디케이터 숨김")
            remoteViews.setViewVisibility(R.id.widget_play_indicator, android.view.View.GONE)
            
            // 실제 위젯 업데이트
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, MusicPlayerWidgetProvider::class.java)
            android.util.Log.d("WidgetProvider", "기본 메타데이터로 위젯 업데이트 실행")
            appWidgetManager.updateAppWidget(componentName, remoteViews)
            android.util.Log.d("WidgetProvider", "=== setDefaultMetadata 완료 ===")
        }
    }

    /**
     * 모든 위젯을 업데이트합니다.
     * MediaService에서 호출됩니다.
     */
    fun updateAllWidgets(context: Context) {
        android.util.Log.d("WidgetProvider", "=== updateAllWidgets 시작 ===")
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, MusicPlayerWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            
            android.util.Log.d("WidgetProvider", "발견된 위젯 수: ${appWidgetIds.size}")
            android.util.Log.d("WidgetProvider", "위젯 ID 목록: ${appWidgetIds.contentToString()}")
            
            if (appWidgetIds.isEmpty()) {
                android.util.Log.w("WidgetProvider", "❌ 등록된 위젯이 없습니다!")
                return
            }
            
            for (appWidgetId in appWidgetIds) {
                android.util.Log.d("WidgetProvider", "--- 위젯 $appWidgetId 업데이트 시작 ---")
                updateAppWidget(context, appWidgetManager, appWidgetId)
                android.util.Log.d("WidgetProvider", "--- 위젯 $appWidgetId 업데이트 완료 ---")
            }
            android.util.Log.d("WidgetProvider", "=== 모든 위젯 업데이트 완료 ===")
        } catch (e: Exception) {
            android.util.Log.e("WidgetProvider", "❌ 위젯 업데이트 실패", e)
        }
    }
}
