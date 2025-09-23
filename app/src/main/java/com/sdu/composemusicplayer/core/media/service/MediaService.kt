package com.sdu.composemusicplayer.core.media.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.sdu.composemusicplayer.R

@UnstableApi
class MediaService : MediaSessionService() {
    
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        // MediaService를 단순화하여 null 반환
        // 실제 미디어 세션은 다른 곳에서 관리
        return null
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
    
    override fun onCreate() {
        super.onCreate()
        // 노티피케이션 채널 생성
        createNotificationChannel()
        // 기본 포어그라운드 서비스 시작
        startForeground(NOW_PLAYING_NOTIFICATION_ID, createNotification())
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOW_PLAYING_CHANNEL_ID,
                "음악 재생",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "음악 재생 상태 알림"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOW_PLAYING_CHANNEL_ID)
            .setContentTitle("음악 재생")
            .setContentText("재생 중...")
            .setSmallIcon(R.drawable.music_player_icon)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
    
    companion object {
        private const val NOW_PLAYING_NOTIFICATION_ID = 1
        private const val NOW_PLAYING_CHANNEL_ID = "now_playing_channel"
    }
}
