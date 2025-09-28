package com.sdu.composemusicplayer.presentation.bluetooth

/**
 * 블루투스 기기 정보를 담는 데이터 클래스
 */
data class BluetoothDevice(
    val name: String,
    val type: DeviceType,
    val isConnected: Boolean = false,
)

/**
 * 블루투스 기기 타입 열거형
 */
enum class DeviceType {
    EARPHONES, // 이어폰
    PHONE, // 휴대폰
}
