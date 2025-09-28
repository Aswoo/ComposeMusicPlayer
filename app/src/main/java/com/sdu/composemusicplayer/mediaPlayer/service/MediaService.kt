@file:Suppress("VariableNaming")

package com.sdu.composemusicplayer.mediaPlayer.service

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
import com.sdu.composemusicplayer.mediaPlayer.mediaNotification.MediaNotificationManager
import com.sdu.composemusicplayer.utils.AppStateUtil.isAppInForeground
import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
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

        val sessionActivityPendingIntent =
            packageManager
                ?.getLaunchIntentForPackage(packageName)
                ?.let { sessionIntent ->
                    PendingIntent.getActivity(
                        this,
                        SESSION_INTENT_REQUEST_CODE,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE,
                    )
                }

        mediaSession =
            MediaSession
                .Builder(this, exoPlayer)
                .setSessionActivity(sessionActivityPendingIntent!!)
                .setCallback(MediaButtonCallback())
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

    internal fun handleMediaButtonSingleClick() {
        serviceScope.launch {
            val isPlaying = playerEnvironment.isPlaying().first()
            if (isPlaying) playerEnvironment.pause() else playerEnvironment.resume()
        }
    }

    internal fun handleMediaButtonDoubleClick() {
        serviceScope.launch {
            playerEnvironment.next()
        }
    }

    internal fun handleMediaButtonNext() {
        serviceScope.launch {
            playerEnvironment.next()
        }
    }

    internal fun handleMediaButtonPrevious() {
        serviceScope.launch {
            playerEnvironment.previous()
        }
    }

    @VisibleForTesting(otherwise = PRIVATE)
    internal inner class MediaButtonCallback
        @Suppress("VariableNaming")
        constructor() : MediaSession.Callback {
            private var lastClickTime: Long = 0
            private var lastKeyCode: Int = -1

            override fun onMediaButtonEvent(
                mediaSession: MediaSession,
                controllerInfo: MediaSession.ControllerInfo,
                intent: Intent,
            ): Boolean {
                if (intent.action != Intent.ACTION_MEDIA_BUTTON) return false

                val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return false
                if (keyEvent.action != KeyEvent.ACTION_DOWN) return false

                val currentTime = System.currentTimeMillis()
                val keyCode = keyEvent.keyCode

                when (keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY,
                    KeyEvent.KEYCODE_MEDIA_PAUSE,
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                    -> {
                        val diff = currentTime - lastClickTime
                        if (lastKeyCode == keyCode && diff < DOUBLE_CLICK_TIMEOUT) {
                            // 더블 클릭
                            handleMediaButtonDoubleClick()
                            resetClickState()
                        } else {
                            // 싱글 클릭
                            handleMediaButtonSingleClick()
                            lastClickTime = currentTime
                            lastKeyCode = keyCode
                        }
                        return true
                    }
                    KeyEvent.KEYCODE_MEDIA_NEXT -> {
                        handleMediaButtonNext()
                        return true
                    }
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                        handleMediaButtonPrevious()
                        return true
                    }
                }
                return false
            }

            private fun resetClickState() {
                lastClickTime = 0
                lastKeyCode = -1
            }
        }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            musicNotificationManager.startMusicNotificationService(this, mediaSession)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        exoPlayer.release()
        stopSelf()
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
