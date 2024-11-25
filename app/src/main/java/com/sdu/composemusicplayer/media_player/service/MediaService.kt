package com.sdu.composemusicplayer.media_player.service

import android.app.ActivityManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.sdu.composemusicplayer.media_player.media_notification.MediaNotificationManager
import com.sdu.composemusicplayer.utils.AppStateUtil.isAppInForeground
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi @AndroidEntryPoint
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


                /**
                 * 사용자 액션이나 [stopForeground]에 의해 알림이 취소되었을 때 호출됨
                 *
                 * 동작 과정:
                 * 1. 앱의 현재 상태(백그라운드/포그라운드) 확인
                 * 2. 포그라운드 취소 -> 재생 정지
                 * 3. 백그라운드 취소 -> 서비스 종료
                 *
                 * @param notificationId 취소된 알림의 고유 식별자
                 * @param dismissedByUser true: 사용자가 직접 알림을 닫음, false: 시스템이나 앱에 의해 알림이 닫힘
                 *
                 * @throws SecurityException 필요한 권한이 없는 경우 발생
                 * @see isAppInForeground 앱 상태 확인 메서드
                 */
                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {

                    // 완전 종료: 사용자가 앱을 명시적으로 닫는 경우  onTaskRemoved 호출
//                    val isAppInForeground: Boolean
//                        get() = ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
                    val isBackground = !isAppInForeground(this@MediaService)
                    if (isBackground) {
                        Log.d("MediaService", "App is in background")
                        stopForeground(STOP_FOREGROUND_REMOVE)  // 포그라운드 서비스 종료
                        exoPlayer.release()  // ExoPlayer 리소스 해제
                        mediaSession.release()  // MediaSession 해제
                        stopSelf()  // 서비스 종료
                    } else {
                        Log.d("MediaService", "App is in forground")
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    }
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
        Log.d("MediaService","destroy")

        stopForeground(STOP_FOREGROUND_REMOVE)  // 포그라운드 서비스 종료
        exoPlayer.release()  // ExoPlayer 리소스 해제
        stopSelf()  // 서비스 종료
        musicNotificationManager.unregisterBluetoothReceiver()
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
