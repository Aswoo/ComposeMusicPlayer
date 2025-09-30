package com.sdu.composemusicplayer.core.media.presentation.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerNotificationManager
import com.sdu.composemusicplayer.R

/**
 * A wrapper class for ExoPlayer's PlayerNotificationManager.
 * It sets up the notification shown to the user during audio playback and provides track metadata,
 * playback controls, and other information.
 */
@OptIn(
    UnstableApi::class,
    androidx.media3.common.util.UnstableApi::class,
)
class MediaNotificationManager(
    private val context: Context,
    @Suppress("UnusedPrivateMember") private val sessionToken: SessionToken,
    private val player: Player,
    private val notificationListener: PlayerNotificationManager.NotificationListener,
) {
    lateinit var notificationManager: PlayerNotificationManager
    private var descriptionAdapter: DescriptionAdapter? = null

    @UnstableApi
    fun startMusicNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession,
    ) {
        android.util.Log.d("MediaNotificationManager", "startMusicNotificationService 시작")
        createNotificationChannel()
        buildMusicNotification(mediaSession)
        startForegroundMusicService(mediaSessionService)
        android.util.Log.d("MediaNotificationManager", "startMusicNotificationService 완료")
    }

    fun buildMusicNotification(mediaSession: MediaSession) {
        val mediaController = MediaController.Builder(context, mediaSession.token).buildAsync()

        descriptionAdapter =
            DescriptionAdapter(
                context = context,
                pendingIntent = null,
                mediaController = mediaController,
            )

        notificationManager =
            PlayerNotificationManager.Builder(context, NOW_PLAYING_NOTIFICATION_ID, NOW_PLAYING_CHANNEL_ID)
                .setNotificationListener(notificationListener)
                .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
                .setChannelDescriptionResourceId(R.string.media_notification_channel_description)
                .setChannelNameResourceId(R.string.media_notification_channel)
                .setMediaDescriptionAdapter(descriptionAdapter!!)
                .setSmallIconResourceId(R.drawable.music_player_icon)
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundMusicService(mediaSessionService: MediaSessionService) {
        // PlayerNotificationManager가 생성한 Notification을 사용
        notificationManager.setPlayer(player)
        
        // 기본 Notification으로 시작 (PlayerNotificationManager가 업데이트함)
        val musicNotification =
            Notification
                .Builder(context, NOW_PLAYING_CHANNEL_ID)
                .setContentTitle("음악 재생 중")
                .setContentText("음악을 재생하고 있습니다")
                .setSmallIcon(R.drawable.music_player_icon)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .build()

        mediaSessionService.startForeground(NOW_PLAYING_NOTIFICATION_ID, musicNotification)
    }

    fun unregisterBluetoothReceiver() {
        descriptionAdapter?.unregisterBluetoothReceiver(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        android.util.Log.d("MediaNotificationManager", "Notification Channel 생성 시작")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channel = NotificationChannel(
            NOW_PLAYING_CHANNEL_ID,
            context.getString(R.string.media_notification_channel),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.media_notification_channel_description)
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        
        notificationManager.createNotificationChannel(channel)
        android.util.Log.d("MediaNotificationManager", "Notification Channel 생성 완료: ${channel.id}")
    }
}

/**
 * The size of the large icon for the notification in pixels.
 */
const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

/**
 * The ID of the notification for the ongoing playback.
 */
const val NOW_PLAYING_NOTIFICATION_ID = 0xb339
const val NOW_PLAYING_CHANNEL_ID = "com.sdu.composemusicplayer.media"
