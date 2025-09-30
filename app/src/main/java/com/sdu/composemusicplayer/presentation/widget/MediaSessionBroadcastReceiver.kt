package com.sdu.composemusicplayer.presentation.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player
import com.sdu.composemusicplayer.core.media.presentation.service.MediaService

/**
 * MediaSession 상태 변경을 감지하여 위젯을 업데이트하는 BroadcastReceiver
 */
class MediaSessionBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("MediaSessionBroadcastReceiver", "onReceive: ${intent.action}")
        
        when (intent.action) {
            "ACTION_PLAY_PAUSE" -> {
                android.util.Log.d("MediaSessionBroadcastReceiver", "재생/일시정지 버튼 클릭")
                // MediaSession을 통해 재생/일시정지 제어
                handlePlayPause(context)
            }
            "ACTION_PREVIOUS" -> {
                android.util.Log.d("MediaSessionBroadcastReceiver", "이전 곡 버튼 클릭")
                // MediaSession을 통해 이전 곡 제어
                handlePrevious(context)
            }
            "ACTION_NEXT" -> {
                android.util.Log.d("MediaSessionBroadcastReceiver", "다음 곡 버튼 클릭")
                // MediaSession을 통해 다음 곡 제어
                handleNext(context)
            }
            "com.sdu.composemusicplayer.action.PLAY_PAUSE",
            "com.sdu.composemusicplayer.action.PREVIOUS",
            "com.sdu.composemusicplayer.action.NEXT" -> {
                // MediaSession 액션 발생 시 위젯 업데이트
                BasicWidgetProvider.updateAllWidgets(context)
            }

            // 재생 상태 변경 감지
            "android.media.AUDIO_BECOMING_NOISY" -> {
                // 오디오 출력이 변경될 때 (예: 헤드폰 연결 해제)
                BasicWidgetProvider.updateAllWidgets(context)
            }
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_USER_PRESENT -> {
                // 화면 상태 변경 시 위젯 업데이트
                BasicWidgetProvider.updateAllWidgets(context)
            }
            Player.EVENT_PLAYBACK_STATE_CHANGED.toString(),
            Player.EVENT_IS_PLAYING_CHANGED.toString(),
            Player.EVENT_MEDIA_METADATA_CHANGED.toString(),
            Player.EVENT_MEDIA_ITEM_TRANSITION.toString() -> {
                // ExoPlayer 상태 변경 시 위젯 업데이트
                BasicWidgetProvider.updateAllWidgets(context)
            }
        }
    }

    private fun handlePlayPause(context: Context) {
        android.util.Log.d("MediaSessionBroadcastReceiver", "재생/일시정지 Intent 전송")
        val intent = Intent(context, MediaService::class.java).apply {
            action = "ACTION_PLAY_PAUSE"
        }
        context.startService(intent)
    }

    private fun handlePrevious(context: Context) {
        android.util.Log.d("MediaSessionBroadcastReceiver", "이전 곡 Intent 전송")
        val intent = Intent(context, MediaService::class.java).apply {
            action = "ACTION_PREVIOUS"
        }
        context.startService(intent)
    }

    private fun handleNext(context: Context) {
        android.util.Log.d("MediaSessionBroadcastReceiver", "다음 곡 Intent 전송")
        val intent = Intent(context, MediaService::class.java).apply {
            action = "ACTION_NEXT"
        }
        context.startService(intent)
    }

    companion object {
        /**
         * MediaSession 상태 변경을 알리는 Intent를 생성합니다.
         */
        fun createMediaSessionUpdateIntent(context: Context): Intent {
            return Intent(context, MediaSessionBroadcastReceiver::class.java).apply {
                action = "com.sdu.composemusicplayer.action.PLAY_PAUSE"
            }
        }

        /**
         * 재생 상태 변경을 알리는 Intent를 생성합니다.
         */
        fun createPlaybackStateUpdateIntent(context: Context): Intent {
            return Intent(context, MediaSessionBroadcastReceiver::class.java).apply {
                action = Player.EVENT_IS_PLAYING_CHANGED.toString()
            }
        }
    }
}
