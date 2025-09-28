package com.sdu.composemusicplayer.core.media.notification

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.core.audio.BluetoothVolumeManager

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
    private val player: Player,
    private val notificationListener: PlayerNotificationManager.NotificationListener,
) {
    lateinit var notificationManager: PlayerNotificationManager
    private var descriptionAdapter: DescriptionAdapter? = null
    private var bluetoothVolumeManager: BluetoothVolumeManager? = null

    @UnstableApi
    fun startMusicNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession,
    ) {
        buildMusicNotification(mediaSession)
        startForegroundMusicService(mediaSessionService, mediaSession)
    }

    fun buildMusicNotification(mediaSession: MediaSession) {
        val mediaController = MediaController.Builder(context, mediaSession.token).buildAsync()

        // 블루투스 음량 관리자 초기화
        bluetoothVolumeManager = BluetoothVolumeManager(context)

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
                    // Spotify 스타일: 기본 재생 컨트롤만 사용
                    setUseRewindAction(false) // 이전 곡 버튼 제거
                    setUseFastForwardAction(false) // 다음 곡 버튼 제거
                    setUseRewindActionInCompactView(false)
                    setUseFastForwardActionInCompactView(false)
                    setUseStopAction(false) // 정지 버튼 제거
                    setUsePlayPauseActions(true) // 재생/일시정지 버튼 유지
                }
    }

    private fun startForegroundMusicService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession,
    ) {
        // 임시 노티피케이션 완전 제거 설계
        // PlayerNotificationManager가 즉시 노티피케이션을 생성하고 포어그라운드 시작
        // NotificationListener에서 onNotificationPosted가 호출되면 자동으로 startForeground가 실행됨
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
