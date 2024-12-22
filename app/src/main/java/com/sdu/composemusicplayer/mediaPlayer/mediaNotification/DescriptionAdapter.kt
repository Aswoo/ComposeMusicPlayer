package com.sdu.composemusicplayer.mediaPlayer.mediaNotification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerNotificationManager
import coil.imageLoader
import coil.request.ImageRequest
import com.google.common.util.concurrent.ListenableFuture
import com.sdu.composemusicplayer.utils.BluetoothUtil.getBluetoothConnectedDeviceAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@UnstableApi
class DescriptionAdapter(
    private val context: Context,
    private val controller: ListenableFuture<MediaController>,
    private val onChange: () -> Unit,
) :
    PlayerNotificationManager.MediaDescriptionAdapter {

    var currentIconUri: Uri? = null
    var currentBitmap: Bitmap? = null
    private var currentBluetoothDeviceName: String? = null
    private var bluetoothReceiver: android.content.BroadcastReceiver? = null
    private var bluetoothJob: Job? = null

    init {
        // Bluetooth 상태를 감지하기 위한 BroadcastReceiver 등록
        registerBluetoothReceiver(context)
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun createCurrentContentIntent(player: Player): PendingIntent? =
        controller.get().sessionActivity

    override fun getCurrentSubText(player: Player): CharSequence? {
        if (currentBluetoothDeviceName == null && bluetoothJob == null) {
            bluetoothJob = serviceScope.launch {
                // 비동기 작업 시작
                currentBluetoothDeviceName = getBluetoothConnectedDeviceAsync(context)
                updateNotification()
            }
        }
        return currentBluetoothDeviceName
    }

    override fun getCurrentContentText(player: Player) = ""

    override fun getCurrentContentTitle(player: Player): String {
        return controller.get().mediaMetadata.title.toString()
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback,
    ): Bitmap? {
        val iconUri = controller.get().mediaMetadata.artworkUri
        return if (currentIconUri != iconUri || currentBitmap == null) {
            // Cache the bitmap for the current song so that successive calls to
            // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
            currentIconUri = iconUri
            serviceScope.launch {
                currentBitmap = iconUri?.let {
                    resolveUriAsBitmap(it)
                }
                currentBitmap?.let { callback.onBitmap(it) }
            }
            null
        } else {
            currentBitmap
        }
    }

    private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            // Create an ImageRequest
            val request = ImageRequest.Builder(context)
                .data(uri)
                .size(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                .build()

            // Execute the request using the Coil image loader
            val drawable = context.imageLoader.execute(request).drawable

            // Convert the drawable to a Bitmap (if not null)
            drawable?.toBitmap(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
        }
    }

    /**
     * Bluetooth 상태 변화를 감지하는 BroadcastReceiver 등록
     */
    private fun registerBluetoothReceiver(context: Context) {
        val filter = android.content.IntentFilter().apply {
            addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }

        bluetoothReceiver = object : android.content.BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context, intent: android.content.Intent) {
                Log.d("OnReceive", "blutooth")
                when (intent.action) {
                    android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        val device: android.bluetooth.BluetoothDevice? =
                            intent.getParcelableExtra(
                                android.bluetooth.BluetoothDevice.EXTRA_DEVICE,
                            )
                        println(device?.name)
                        currentBluetoothDeviceName = device?.name
                        updateNotification()
                    }

                    android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        currentBluetoothDeviceName = null
                        updateNotification()
                    }
                }
            }
        }
        bluetoothReceiver?.let {
            context.registerReceiver(it, filter)
        }
    }

    /**
     * Notification을 새로고침
     */
    private fun updateNotification() {
        onChange()
    }
    fun unregisterBluetoothReceiver(context: Context) {
        bluetoothReceiver?.let {
            context.unregisterReceiver(it)
            bluetoothReceiver = null
        }
    }
}
