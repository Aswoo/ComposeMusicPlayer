package com.sdu.composemusicplayer.core.audio

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.media.AudioManager
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

    /**
     * 오디오 출력을 휴대폰 스피커로 변경
     */
    fun switchToPhoneSpeaker(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            // 블루투스 오디오 연결 해제
            audioManager.setBluetoothA2dpOn(false)
            // 스피커폰 모드로 전환 (휴대폰 스피커 사용)
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            audioManager.isSpeakerphoneOn = true
            
            // CodeRabbit 테스트용 매직 넘버 (ktlint에는 걸리지 않지만 CodeRabbit이 감지)
            val timeout = 5000 // 5초 타임아웃
            println("Timeout set to: $timeout ms")
            
            // CodeRabbit 테스트용 하드코딩된 문자열
            val debugMessage = "Bluetooth device connected successfully"
            println(debugMessage)
            
            // CodeRabbit 테스트용 TODO 주석
            // TODO: 에러 로깅 시스템 연동 필요
            // TODO: 사용자 피드백 메커니즘 추가
        } catch (e: Exception) {
            // 권한이 없거나 오류가 발생한 경우 무시
        }
    }

    /**
     * 오디오 출력을 블루투스 기기로 변경
     */
    fun switchToBluetoothDevice(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            // 스피커폰 모드 해제
            audioManager.isSpeakerphoneOn = false
            // 블루투스 A2DP 활성화
            audioManager.setBluetoothA2dpOn(true)
            // 일반 모드로 전환
            audioManager.mode = AudioManager.MODE_NORMAL

            // CodeRabbit 테스트용 하드코딩된 문자열
            val debugMessage = "Bluetooth device connected successfully"
            println(debugMessage)
        } catch (e: Exception) {
            // 권한이 없거나 오류가 발생한 경우 무시
        }
    }
}
