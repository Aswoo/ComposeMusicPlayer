package com.sdu.composemusicplayer.core.media.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.sdu.composemusicplayer.MainActivity
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.core.audio.BluetoothVolumeManager
import com.sdu.composemusicplayer.core.audio.BluetoothVolumeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 블루투스 음량 조절 기능이 포함된 커스텀 노티피케이션 매니저
 */
class BluetoothVolumeNotificationManager(
    private val context: Context,
    private val sessionToken: SessionToken,
    private val player: Player,
    private val mediaSession: MediaSession
) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val bluetoothVolumeManager = BluetoothVolumeManager(context)
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private var currentBluetoothInfo: BluetoothVolumeInfo? = null
    
    companion object {
        private const val CHANNEL_ID = "bluetooth_volume_notification"
        private const val CHANNEL_NAME = "블루투스 음량 노티피케이션"
        private const val CHANNEL_DESCRIPTION = "블루투스 이어폰 음량 조절 노티피케이션"
        private const val NOTIFICATION_ID = 0xb340
    }
    
    init {
        createNotificationChannel()
        observeBluetoothVolumeChanges()
    }
    
    /**
     * 노티피케이션 채널을 생성합니다.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = CHANNEL_DESCRIPTION
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * 블루투스 음량 변화를 관찰합니다.
     */
    private fun observeBluetoothVolumeChanges() {
        serviceScope.launch {
            bluetoothVolumeManager.isBluetoothAudioConnected.collectLatest { isConnected ->
                if (isConnected) {
                    currentBluetoothInfo = bluetoothVolumeManager.getBluetoothVolumeInfo()
                    updateNotification()
                } else {
                    currentBluetoothInfo = null
                    updateNotification()
                }
            }
        }
        
        serviceScope.launch {
            bluetoothVolumeManager.bluetoothVolume.collectLatest { volume ->
                currentBluetoothInfo = bluetoothVolumeManager.getBluetoothVolumeInfo()
                updateNotification()
            }
        }
    }
    
    /**
     * 노티피케이션을 업데이트합니다.
     */
    fun updateNotification() {
        val notification = buildNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * 노티피케이션을 생성합니다.
     */
    private fun buildNotification(): Notification {
        val remoteViews = createRemoteViews()
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.music_player_icon)
            .setCustomBigContentView(remoteViews)
            .setCustomContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
    
    /**
     * RemoteViews를 생성합니다.
     */
    private fun createRemoteViews(): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.bluetooth_volume_notification)
        
        // 음악 정보 설정
        setupMusicInfo(remoteViews)
        
        // 재생 컨트롤 버튼 설정
        setupPlaybackControls(remoteViews)
        
        // 블루투스 음량 조절 UI 설정
        setupVolumeControls(remoteViews)
        
        return remoteViews
    }
    
    /**
     * 음악 정보를 설정합니다.
     */
    private fun setupMusicInfo(remoteViews: RemoteViews) {
        val mediaController = MediaController.Builder(context, sessionToken).buildAsync()
        
        mediaController.get().addListener(object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) {
                remoteViews.setTextViewText(R.id.title, mediaMetadata.title ?: "알 수 없는 제목")
                remoteViews.setTextViewText(R.id.artist, mediaMetadata.artist ?: "알 수 없는 아티스트")
                
                // 앨범 아트 설정 (간단한 구현)
                // 실제로는 이미지 로딩 라이브러리를 사용해야 합니다.
            }
        })
        
        // 블루투스 기기 정보 설정
        val bluetoothInfo = currentBluetoothInfo
        val deviceText = if (bluetoothInfo?.isConnected == true) {
            "${bluetoothInfo.deviceName} (음량: ${bluetoothInfo.currentVolume}/${bluetoothInfo.maxVolume})"
        } else {
            "현재 휴대 전화"
        }
        remoteViews.setTextViewText(R.id.bluetooth_device, deviceText)
    }
    
    /**
     * 재생 컨트롤 버튼을 설정합니다.
     */
    private fun setupPlaybackControls(remoteViews: RemoteViews) {
        val sessionActivityPendingIntent = createSessionActivityPendingIntent()
        
        // 이전 버튼
        val previousIntent = createMediaButtonIntent(android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        remoteViews.setOnClickPendingIntent(R.id.btn_previous, previousIntent)
        
        // 재생/일시정지 버튼
        val playPauseIntent = createMediaButtonIntent(android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        remoteViews.setOnClickPendingIntent(R.id.btn_play_pause, playPauseIntent)
        
        // 다음 버튼
        val nextIntent = createMediaButtonIntent(android.view.KeyEvent.KEYCODE_MEDIA_NEXT)
        remoteViews.setOnClickPendingIntent(R.id.btn_next, nextIntent)
        
        // 재생 상태에 따른 아이콘 변경
        val isPlaying = player.isPlaying
        val playPauseIcon = if (isPlaying) R.drawable.ic_pause_filled_rounded else R.drawable.ic_play_arrow
        remoteViews.setImageViewResource(R.id.btn_play_pause, playPauseIcon)
    }
    
    /**
     * 블루투스 음량 조절 UI를 설정합니다.
     */
    private fun setupVolumeControls(remoteViews: RemoteViews) {
        val bluetoothInfo = currentBluetoothInfo
        
        if (bluetoothInfo?.isConnected == true) {
            // 블루투스 연결 시 음량 조절 UI 표시
            remoteViews.setViewVisibility(R.id.volume_controls, android.view.View.VISIBLE)
            
            // 음량 텍스트 설정
            remoteViews.setTextViewText(R.id.volume_text, "${bluetoothInfo.currentVolume}/${bluetoothInfo.maxVolume}")
            
            // 음량 슬라이더 설정
            remoteViews.setProgressBar(R.id.volume_seekbar, bluetoothInfo.maxVolume, bluetoothInfo.currentVolume, false)
            
            // 음량 슬라이더 클릭 이벤트 설정
            val volumeUpIntent = createMediaButtonIntent(android.view.KeyEvent.KEYCODE_VOLUME_UP)
            val volumeDownIntent = createMediaButtonIntent(android.view.KeyEvent.KEYCODE_VOLUME_DOWN)
            
            // SeekBar의 경우 직접적인 클릭 이벤트가 어려우므로
            // 볼륨 업/다운 버튼을 추가로 제공할 수 있습니다.
        } else {
            // 블루투스 연결 해제 시 음량 조절 UI 숨김
            remoteViews.setViewVisibility(R.id.volume_controls, android.view.View.GONE)
        }
    }
    
    /**
     * 세션 액티비티 PendingIntent를 생성합니다.
     */
    private fun createSessionActivityPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    /**
     * 미디어 버튼 Intent를 생성합니다.
     */
    private fun createMediaButtonIntent(keyCode: Int): PendingIntent {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            putExtra(Intent.EXTRA_KEY_EVENT, android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, keyCode))
        }
        
        return PendingIntent.getBroadcast(
            context,
            keyCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    /**
     * 노티피케이션을 제거합니다.
     */
    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
