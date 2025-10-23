package com.sdu.composemusicplayer.core.media.presentation.notification

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
import kotlinx.coroutines.launch

/**
 * A custom implementation of PlayerNotificationManager.MediaDescriptionAdapter
 * that provides track metadata for the notification.
 */
@UnstableApi
class DescriptionAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?,
    private val mediaController: ListenableFuture<MediaController>,
) : PlayerNotificationManager.MediaDescriptionAdapter {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var bluetoothReceiver: android.content.BroadcastReceiver? = null

    override fun getCurrentContentTitle(player: Player): CharSequence {
        val mediaMetadata = player.mediaMetadata
        return if (mediaMetadata.title != null) {
            mediaMetadata.title.toString()
        } else {
            "Unknown Title"
        }
    }

    override fun getCurrentContentText(player: Player): CharSequence {
        val mediaMetadata = player.mediaMetadata
        return if (mediaMetadata.artist != null) {
            mediaMetadata.artist.toString()
        } else {
            "Unknown Artist"
        }
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback,
    ): Bitmap? {
        val mediaMetadata = player.mediaMetadata
        val albumArtUri = mediaMetadata.artworkUri

        return if (albumArtUri != null && albumArtUri != Uri.EMPTY) {
            try {
                val imageRequest =
                    ImageRequest
                        .Builder(context)
                        .data(albumArtUri)
                        .size(NOTIFICATION_LARGE_ICON_SIZE)
                        .build()

                scope.launch {
                    try {
                        val result = context.imageLoader.execute(imageRequest)
                        val drawable = result.drawable
                        val bitmap = drawable?.toBitmap(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                        callback.onBitmap(
                            bitmap ?: android.graphics.Bitmap.createBitmap(
                                1, 1,
                                android
                                    .graphics
                                    .Bitmap
                                    .Config
                                    .ARGB_8888,
                            ),
                        )
                    } catch (e: Exception) {
                        Log.e("DescriptionAdapter", "Error loading album art", e)
                        callback.onBitmap(
                            android.graphics.Bitmap.createBitmap(
                                1,
                                1,
                                android
                                    .graphics
                                    .Bitmap
                                    .Config
                                    .ARGB_8888,
                            ),
                        )
                    }
                }
                null
            } catch (e: Exception) {
                Log.e("DescriptionAdapter", "Error creating image request", e)
                null
            }
        } else {
            null
        }
    }

    override fun getCurrentSubText(player: Player): CharSequence? {
        val mediaMetadata = player.mediaMetadata
        return mediaMetadata.albumTitle?.toString()
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        return pendingIntent
    }

    @SuppressLint("MissingPermission")
    fun registerBluetoothReceiver(context: Context) {
        if (bluetoothReceiver == null) {
            bluetoothReceiver =
                object : android.content.BroadcastReceiver() {
                    override fun onReceive(
                        context: Context,
                        intent: android.content.Intent,
                    ) {
                        when (intent.action) {
                            android.bluetooth.BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                                scope.launch {
                                    val device = getBluetoothConnectedDeviceAsync(context)
                                    if (device != null) {
                                        Log.d("DescriptionAdapter", "Bluetooth device connected")
                                    }
                                }
                            }
                        }
                    }
                }

            val filter =
                android.content.IntentFilter().apply {
                    addAction(android.bluetooth.BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                }
            context.registerReceiver(bluetoothReceiver, filter)
        }
    }

    fun unregisterBluetoothReceiver(context: Context) {
        bluetoothReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                Log.e("DescriptionAdapter", "Error unregistering Bluetooth receiver", e)
            }
            bluetoothReceiver = null
        }
    }
}
