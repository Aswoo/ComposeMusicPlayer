package com.sdu.composemusicplayer.mediaPlayer.mediaNotification

import android.app.Notification
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
 * such as track title and icon image.
 * @param context The context used to create the notification.
 * @param sessionToken The session token used to build MediaController.
 * @param player The ExoPlayer instance.
 * @param notificationListener The listener for notification events.
 */
@androidx.annotation.OptIn(
    androidx
        .media3
        .common
        .util
        .UnstableApi::class,
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
        buildMusicNotification(mediaSession)
        startForegroundMusicService(mediaSessionService)
    }

    fun buildMusicNotification(mediaSession: MediaSession) {
        val mediaController = MediaController.Builder(context, mediaSession.token).buildAsync()

        descriptionAdapter =
            DescriptionAdapter(context, mediaController) {
                notificationManager.invalidate()
            }

        notificationManager =
            PlayerNotificationManager
                .Builder(
                    context,
                    NOW_PLAYING_NOTIFICATION_ID,
                    NOW_PLAYING_CHANNEL_ID,
                ).setChannelNameResourceId(R.string.media_notification_channel)
                .setChannelDescriptionResourceId(R.string.media_notification_channel_description)
                .setMediaDescriptionAdapter(descriptionAdapter!!)
                .setNotificationListener(notificationListener)
                .setSmallIconResourceId(R.drawable.music_player_icon)
                .build()
                .apply {
                    setPlayer(player)
                    setUseRewindAction(true)
                    setUseFastForwardAction(true)
                    setUseRewindActionInCompactView(true)
                    setUseFastForwardActionInCompactView(true)
                    setUseRewindActionInCompactView(true)
                }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundMusicService(mediaSessionService: MediaSessionService) {
        val musicNotification =
            Notification
                .Builder(context, NOW_PLAYING_CHANNEL_ID)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()

        mediaSessionService.startForeground(NOW_PLAYING_NOTIFICATION_ID, musicNotification)
    }

    fun unregisterBluetoothReceiver() {
        descriptionAdapter?.unregisterBluetoothReceiver(context)
    }
}

/**
 * The size of the large icon for the notification in pixels.
 */
const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

/**
 * The channel ID for the notification.
 */
const val NOW_PLAYING_CHANNEL_ID = "media.NOW_PLAYING"

/**
 * The notification ID.
 */
const val NOW_PLAYING_NOTIFICATION_ID = 0xb339 // Arbitrary number used to identify our notification
