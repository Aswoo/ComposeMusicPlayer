package com.sdu.composemusicplayer.core.audio

import android.content.Context
import android.media.AudioManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 오디오 음량 관리를 담당하는 클래스
 * 블루투스 이어폰 연결 상태에 따른 음량 제어를 제공합니다.
 */
class AudioVolumeManager(private val context: Context) {
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _currentVolume = MutableStateFlow(getCurrentVolume())
    val currentVolume: StateFlow<Int> = _currentVolume.asStateFlow()

    private val _isBluetoothConnected = MutableStateFlow(isBluetoothAudioConnected())
    val isBluetoothConnected: StateFlow<Boolean> = _isBluetoothConnected.asStateFlow()

    companion object {
        private const val TAG = "AudioVolumeManager"
    }

    /**
     * 현재 음량을 반환합니다.
     * @return 현재 음량 (0 ~ maxVolume)
     */
    fun getCurrentVolume(): Int {
        return try {
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        } catch (e: Exception) {
            Log.e(TAG, "음량 조회 실패", e)
            0
        }
    }

    /**
     * 최대 음량을 반환합니다.
     * @return 최대 음량
     */
    fun getMaxVolume(): Int {
        return try {
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        } catch (e: Exception) {
            Log.e(TAG, "최대 음량 조회 실패", e)
            15 // 기본값
        }
    }

    /**
     * 음량을 설정합니다.
     * @param volume 설정할 음량 (0 ~ maxVolume)
     */
    fun setVolume(volume: Int) {
        try {
            val maxVolume = getMaxVolume()
            val adjustedVolume = volume.coerceIn(0, maxVolume)

            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                adjustedVolume,
                // 플래그 없음
                0,
            )

            _currentVolume.value = adjustedVolume
            Log.d(TAG, "음량 설정: $adjustedVolume/$maxVolume")
        } catch (e: Exception) {
            Log.e(TAG, "음량 설정 실패", e)
        }
    }

    /**
     * 음량을 조절합니다.
     * @param direction AudioManager.ADJUST_RAISE, ADJUST_LOWER, ADJUST_SAME 중 하나
     */
    fun adjustVolume(direction: Int) {
        try {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                direction,
                // 플래그 없음
                0,
            )

            _currentVolume.value = getCurrentVolume()
            Log.d(TAG, "음량 조절: $direction, 현재 음량: ${_currentVolume.value}")
        } catch (e: Exception) {
            Log.e(TAG, "음량 조절 실패", e)
        }
    }

    /**
     * 블루투스 오디오가 연결되어 있는지 확인합니다.
     * @return 블루투스 오디오 연결 상태
     */
    fun isBluetoothAudioConnected(): Boolean {
        return try {
            audioManager.isBluetoothA2dpOn
        } catch (e: Exception) {
            Log.e(TAG, "블루투스 연결 상태 확인 실패", e)
            false
        }
    }

    /**
     * 현재 오디오 출력 장치 정보를 반환합니다.
     * @return 오디오 출력 장치 정보
     */
    fun getAudioOutputInfo(): String {
        return when {
            audioManager.isBluetoothA2dpOn -> "블루투스 이어폰"
            audioManager.isWiredHeadsetOn -> "유선 이어폰"
            audioManager.isSpeakerphoneOn -> "스피커"
            else -> "기본 스피커"
        }
    }

    /**
     * 음량 상태를 업데이트합니다.
     * 블루투스 연결 상태 변화 시 호출되어야 합니다.
     */
    fun updateVolumeState() {
        _currentVolume.value = getCurrentVolume()
        _isBluetoothConnected.value = isBluetoothAudioConnected()
        Log.d(TAG, "음량 상태 업데이트: ${_currentVolume.value}/${getMaxVolume()}, 블루투스: ${_isBluetoothConnected.value}")
    }

    /**
     * 음량을 퍼센트로 반환합니다.
     * @return 음량 퍼센트 (0.0 ~ 100.0)
     */
    fun getVolumePercentage(): Float {
        val current = getCurrentVolume()
        val max = getMaxVolume()
        return if (max > 0) (current.toFloat() / max.toFloat()) * 100f else 0f
    }

    /**
     * 퍼센트로 음량을 설정합니다.
     * @param percentage 설정할 음량 퍼센트 (0.0 ~ 100.0)
     */
    fun setVolumePercentage(percentage: Float) {
        val maxVolume = getMaxVolume()
        val volume = ((percentage / 100f) * maxVolume).toInt()
        setVolume(volume)
    }
}
