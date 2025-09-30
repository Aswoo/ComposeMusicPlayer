@file:Suppress("VariableNaming")

package com.sdu.composemusicplayer.core.media.presentation.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.sdu.composemusicplayer.core.media.presentation.notification.MediaNotificationManager
import com.sdu.composemusicplayer.utils.AppStateUtil.isAppInForeground
import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
import com.sdu.composemusicplayer.presentation.widget.BasicWidgetProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DOUBLE_CLICK_TIMEOUT = 500L

@UnstableApi
@AndroidEntryPoint
class MediaService : MediaSessionService() {
    lateinit var mediaSession: MediaSession
    lateinit var musicNotificationManager: MediaNotificationManager
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var playerEnvironment: IPlayerEnvironment

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("MediaService", "onCreate 호출됨")

        val sessionActivityPendingIntent =
            packageManager
                ?.getLaunchIntentForPackage(packageName)
                ?.let { sessionIntent ->
                    PendingIntent.getActivity(
                        this,
                        0,
                        sessionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                }

        mediaSession =
            MediaSession.Builder(this, exoPlayer)
                .setSessionActivity(sessionActivityPendingIntent ?: PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, com.sdu.composemusicplayer.MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                ))
                .build()

        musicNotificationManager =
            MediaNotificationManager(
                context = this,
                sessionToken = mediaSession.token,
                player = exoPlayer,
                notificationListener =
                    object : PlayerNotificationManager.NotificationListener {
                        override fun onNotificationPosted(
                            notificationId: Int,
                            notification: Notification,
                            ongoing: Boolean,
                        ) {
                            // 필요시 구현
                        }

                        override fun onNotificationCancelled(
                            notificationId: Int,
                            dismissedByUser: Boolean,
                        ) {
                            if (!isAppInForeground(this@MediaService)) {
                                stopForeground(STOP_FOREGROUND_REMOVE)
                                exoPlayer.release()
                                mediaSession.release()
                                stopSelf()
                            } else {
                                stopForeground(STOP_FOREGROUND_REMOVE)
                            }
                        }
                    },
            )
    }

    @VisibleForTesting(otherwise = PRIVATE)
    internal fun handleMediaButtonSingleClick() {
        serviceScope.launch {
            val isPlaying = playerEnvironment.isPlaying().first()
            if (isPlaying) playerEnvironment.pause() else playerEnvironment.resume()
        }
    }

    @VisibleForTesting(otherwise = PRIVATE)
    internal fun handleMediaButtonDoubleClick() {
        serviceScope.launch {
            playerEnvironment.next()
        }
    }

    @VisibleForTesting(otherwise = PRIVATE)
    internal fun handleMediaButtonNext() {
        serviceScope.launch {
            playerEnvironment.next()
        }
    }

    @VisibleForTesting(otherwise = PRIVATE)
    internal fun handleMediaButtonPrevious() {
        serviceScope.launch {
            playerEnvironment.previous()
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        android.util.Log.d("MediaService", "onStartCommand 호출됨")
        
        // Intent 액션 처리
        when (intent?.action) {
               "ACTION_PLAY_PAUSE" -> {
                   android.util.Log.d("MediaService", "재생/일시정지 명령 수신")
                   serviceScope.launch {
                       val isPlaying = playerEnvironment.isPlaying().first()
                       if (isPlaying) {
                           playerEnvironment.pause()
                           android.util.Log.d("MediaService", "일시정지 실행")
                       } else {
                           playerEnvironment.resume()
                           android.util.Log.d("MediaService", "재생 실행")
                       }
                       
                       // 상태 변경 후 즉시 위젯 업데이트 (깜박임 방지)
                       BasicWidgetProvider.updateAllWidgets(this@MediaService)
                   }
               }
            "ACTION_PREVIOUS" -> {
                android.util.Log.d("MediaService", "이전 곡 명령 수신")
                serviceScope.launch {
                    playerEnvironment.previous()
                    android.util.Log.d("MediaService", "이전 곡 실행")
                    
                    // 상태 변경 후 즉시 위젯 업데이트 (깜박임 방지)
                    BasicWidgetProvider.updateAllWidgets(this@MediaService)
                }
            }
            "ACTION_NEXT" -> {
                android.util.Log.d("MediaService", "다음 곡 명령 수신")
                serviceScope.launch {
                    playerEnvironment.next()
                    android.util.Log.d("MediaService", "다음 곡 실행")
                    
                    // 상태 변경 후 즉시 위젯 업데이트 (깜박임 방지)
                    BasicWidgetProvider.updateAllWidgets(this@MediaService)
                }
            }
            else -> {
                // 일반적인 서비스 시작
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    android.util.Log.d("MediaService", "Notification 서비스 시작")
                    musicNotificationManager.startMusicNotificationService(this, mediaSession)
                }
                
                // MediaService 시작 후 위젯 업데이트
                serviceScope.launch {
                    android.util.Log.d("MediaService", "위젯 업데이트 시작 (2초 후)")
                    kotlinx.coroutines.delay(2000) // MediaSession이 완전히 준비될 때까지 대기
                    android.util.Log.d("MediaService", "위젯 업데이트 실행")
                    BasicWidgetProvider.updateAllWidgets(this@MediaService)
                }
            }
        }
        
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        mediaSession.release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }


    companion object {
        private var lastClickTime = 0L
        private var lastKeyCode = -1

        @JvmStatic
        fun handleMediaButtonEvent(
            context: android.content.Context,
            intent: android.content.Intent,
        ): Boolean {
            val keyEvent = intent.getParcelableExtra<KeyEvent>(android.content.Intent.EXTRA_KEY_EVENT)
            if (keyEvent?.action != KeyEvent.ACTION_DOWN) {
                return false
            }

            val currentTime = System.currentTimeMillis()
            val keyCode = keyEvent.keyCode

            return when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    if (currentTime - lastClickTime < DOUBLE_CLICK_TIMEOUT && lastKeyCode == keyCode) {
                        // Double click
                        lastClickTime = 0
                        lastKeyCode = -1
                        // Handle double click if needed
                        false
                    } else {
                        // Single click
                        lastClickTime = currentTime
                        lastKeyCode = keyCode
                        true
                    }
                }
                else -> {
                    lastClickTime = 0
                    lastKeyCode = -1
                    false
                }
            }
        }
    }
}
