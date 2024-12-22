package com.sdu.composemusicplayer.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

object BluetoothUtil {

    suspend fun getBluetoothConnectedDeviceAsync(context: Context): String? {
        return suspendCancellableCoroutine { continuation ->
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: run {
                continuation.resume("No Bluetooth Adapter") // 이미 한 번만 호출
                return@suspendCancellableCoroutine
            }

            val serviceListener = object : BluetoothProfile.ServiceListener {
                @SuppressLint("MissingPermission")
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    if (profile == BluetoothProfile.A2DP) {
                        val connectedDevices = proxy.connectedDevices
                        val deviceName = if (connectedDevices.isNotEmpty()) {
                            connectedDevices[0].name ?: "Unknown Device"
                        } else {
                            "현재 휴대 전화"
                        }

                        continuation.resume(deviceName) // 여기에서만 한 번 호출
                        bluetoothAdapter.closeProfileProxy(profile, proxy)
                    }
                }

                override fun onServiceDisconnected(profile: Int) {
                    // service가 끊어졌을 때 resume 호출
                    if (!continuation.isCompleted) {
                        continuation.resume("Disconnected") // 이미 종료되지 않으면 resume
                    }
                }
            }

            bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.A2DP)

            // 취소 시 처리
        }
    }
}
