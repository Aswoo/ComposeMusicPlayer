package com.sdu.composemusicplayer.media_player.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.sdu.composemusicplayer.media_player.media_notification.MediaNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaService : MediaSessionService() {

    lateinit var mediaSession: MediaSession
    lateinit var musicNotificationManager: MediaNotificationManager

    @Inject
    lateinit var exoPlayer: ExoPlayer // Hilt DI로 ExoPlayer 주입받기

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
            this.packageManager?.getLaunchIntentForPackage(this.packageName)
                ?.let { sessionIntent ->
                    PendingIntent.getActivity(
                        this,
                        SESSION_INTENT_REQUEST_CODE,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }

        // MediaSession 초기화
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent!!) // Session activity 설정
            .build()

        // 미디어 세션에서 미디어 세션 서비스와 연동된 notification 설정
        musicNotificationManager = MediaNotificationManager(
            context = this,
            sessionToken = mediaSession.token,
            player = exoPlayer,
            notificationListener = object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {

                }

                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {

                }
            }
        )
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            musicNotificationManager.startMusicNotificationService(
                mediaSession = mediaSession,
                mediaSessionService = this
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.apply {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
        }
    }
    companion object {
        const val SESSION_INTENT_REQUEST_CODE = 0
    }
}
