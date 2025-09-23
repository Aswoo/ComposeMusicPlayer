package com.sdu.composemusicplayer.core.audio

import android.content.Context
import android.media.AudioManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.sdu.composemusicplayer.core.audio.BluetoothUtil.getBluetoothConnectedDeviceAsync

/**
 * 블루투스 이어폰 전용 음량 관리 클래스
 * 블루투스 연결 상태와 음량을 통합 관리합니다.
 */
class BluetoothVolumeManager(private val context: Context) {
    
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    private val _isBluetoothAudioConnected = MutableStateFlow(false)
    val isBluetoothAudioConnected: StateFlow<Boolean> = _isBluetoothAudioConnected.asStateFlow()
    
    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()
    
    private val _bluetoothVolume = MutableStateFlow(0)
    val bluetoothVolume: StateFlow<Int> = _bluetoothVolume.asStateFlow()
    
    
    companion object {
        private const val TAG = "BluetoothVolumeManager"
        private const val BLUETOOTH_VOLUME_MAX = 15 // 블루투스 이어폰 최대 음량
    }
    
    init {
        updateBluetoothConnectionState()
    }
    
    /**
     * 블루투스 오디오 연결 상태를 확인합니다.
     * @return 블루투스 오디오 연결 상태
     */
    fun isBluetoothAudioConnected(): Boolean {
        return try {
            audioManager.isBluetoothA2dpOn
        } catch (e: Exception) {
            Log.e(TAG, "블루투스 오디오 연결 상태 확인 실패", e)
            false
        }
    }
    
    /**
     * 블루투스 음량을 가져옵니다.
     * @return 블루투스 음량 (0 ~ BLUETOOTH_VOLUME_MAX)
     */
    fun getBluetoothVolume(): Int {
        return if (isBluetoothAudioConnected()) {
            try {
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            } catch (e: Exception) {
                Log.e(TAG, "블루투스 음량 조회 실패", e)
                0
            }
        } else {
            0
        }
    }
    
    /**
     * 블루투스 음량을 설정합니다.
     * @param volume 설정할 음량 (0 ~ BLUETOOTH_VOLUME_MAX)
     */
    fun setBluetoothVolume(volume: Int) {
        if (!isBluetoothAudioConnected()) {
            Log.w(TAG, "블루투스 오디오가 연결되지 않음")
            return
        }
        
        try {
            val adjustedVolume = volume.coerceIn(0, BLUETOOTH_VOLUME_MAX)
            
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                adjustedVolume,
                AudioManager.FLAG_SHOW_UI // 음량 변경 시 UI 표시
            )
            
            _bluetoothVolume.value = adjustedVolume
            Log.d(TAG, "블루투스 음량 설정: $adjustedVolume/$BLUETOOTH_VOLUME_MAX")
        } catch (e: Exception) {
            Log.e(TAG, "블루투스 음량 설정 실패", e)
        }
    }
    
    /**
     * 블루투스 음량을 조절합니다.
     * @param direction AudioManager.ADJUST_RAISE, ADJUST_LOWER, ADJUST_SAME 중 하나
     */
    fun adjustBluetoothVolume(direction: Int) {
        if (!isBluetoothAudioConnected()) {
            Log.w(TAG, "블루투스 오디오가 연결되지 않음")
            return
        }
        
        try {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                direction,
                AudioManager.FLAG_SHOW_UI
            )
            
            _bluetoothVolume.value = getBluetoothVolume()
            Log.d(TAG, "블루투스 음량 조절: $direction, 현재 음량: ${_bluetoothVolume.value}")
        } catch (e: Exception) {
            Log.e(TAG, "블루투스 음량 조절 실패", e)
        }
    }
    
    /**
     * 연결된 블루투스 기기 이름을 비동기로 가져옵니다.
     * @return 연결된 블루투스 기기 이름
     */
    suspend fun getBluetoothConnectedDeviceNameAsync(): String? {
        return getBluetoothConnectedDeviceAsync(context)
    }
    
    /**
     * 블루투스 연결 상태를 업데이트합니다.
     */
    fun updateBluetoothConnectionState() {
        val isConnected = isBluetoothAudioConnected()
        _isBluetoothAudioConnected.value = isConnected
        
        if (isConnected) {
            _bluetoothVolume.value = getBluetoothVolume()
            // 비동기로 기기 이름 업데이트
            updateConnectedDeviceName()
        } else {
            _bluetoothVolume.value = 0
            _connectedDeviceName.value = null
        }
        
        Log.d(TAG, "블루투스 연결 상태 업데이트: $isConnected, 음량: ${_bluetoothVolume.value}")
    }
    
    /**
     * 연결된 기기 이름을 업데이트합니다.
     */
    private fun updateConnectedDeviceName() {
        // 코루틴 스코프를 사용하여 비동기로 기기 이름을 가져옴
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val deviceName = getBluetoothConnectedDeviceAsync(context)
                _connectedDeviceName.value = deviceName
                Log.d(TAG, "블루투스 기기 이름 업데이트: $deviceName")
            } catch (e: Exception) {
                Log.e(TAG, "블루투스 기기 이름 가져오기 실패", e)
                _connectedDeviceName.value = null
            }
        }
    }
    
    /**
     * 블루투스 음량을 퍼센트로 반환합니다.
     * @return 음량 퍼센트 (0.0 ~ 100.0)
     */
    fun getBluetoothVolumePercentage(): Float {
        val current = getBluetoothVolume()
        return if (BLUETOOTH_VOLUME_MAX > 0) {
            (current.toFloat() / BLUETOOTH_VOLUME_MAX.toFloat()) * 100f
        } else {
            0f
        }
    }
    
    /**
     * 퍼센트로 블루투스 음량을 설정합니다.
     * @param percentage 설정할 음량 퍼센트 (0.0 ~ 100.0)
     */
    fun setBluetoothVolumePercentage(percentage: Float) {
        val volume = ((percentage / 100f) * BLUETOOTH_VOLUME_MAX).toInt()
        setBluetoothVolume(volume)
    }
    
    /**
     * 블루투스 음량 상태 정보를 반환합니다.
     * @return 블루투스 음량 상태 정보
     */
    fun getBluetoothVolumeInfo(): BluetoothVolumeInfo {
        return BluetoothVolumeInfo(
            isConnected = isBluetoothAudioConnected(),
            currentVolume = getBluetoothVolume(),
            maxVolume = BLUETOOTH_VOLUME_MAX,
            deviceName = _connectedDeviceName.value,
            volumePercentage = getBluetoothVolumePercentage()
        )
    }
}

/**
 * 블루투스 음량 정보를 담는 데이터 클래스
 */
data class BluetoothVolumeInfo(
    val isConnected: Boolean,
    val currentVolume: Int,
    val maxVolume: Int,
    val deviceName: String?,
    val volumePercentage: Float
)
