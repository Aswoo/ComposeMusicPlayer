package com.sdu.composemusicplayer.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object BluetoothUtil {
    suspend fun getBluetoothConnectedDeviceAsync(context: Context): String? {
        return suspendCancellableCoroutine { continuation ->
            val bluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter() ?: run {
                    continuation.resume("No Bluetooth Adapter")
                    return@suspendCancellableCoroutine
                }

            val serviceListener =
                object : BluetoothProfile.ServiceListener {
                    @SuppressLint("MissingPermission")
                    override fun onServiceConnected(
                        profile: Int,
                        proxy: BluetoothProfile,
                    ) {
                        if (profile == BluetoothProfile.A2DP) {
                            val connectedDevices = proxy.connectedDevices
                            val deviceName =
                                if (connectedDevices.isNotEmpty()) {
                                    connectedDevices[0].name
                                        ?: "Unknown Device"
                                } else {
                                    "현재 휴대 전화"
                                }

                            continuation.resume(deviceName)
                            bluetoothAdapter.closeProfileProxy(profile, proxy)
                        }
                    }

                    override fun onServiceDisconnected(profile: Int) {
                        if (!continuation.isCompleted) {
                            continuation.resume("Disconnected")
                        }
                    }
                }

            bluetoothAdapter.getProfileProxy(
                context,
                serviceListener,
                BluetoothProfile.A2DP,
            )
        }
    }
}
