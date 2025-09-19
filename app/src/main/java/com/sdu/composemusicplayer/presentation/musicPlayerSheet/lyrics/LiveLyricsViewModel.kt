@file:Suppress("ImplicitDefaultLocale")
package com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics

import androidx.compose.ui.semantics.text
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.composemusicplayer.core.constants.AppConstants
import com.sdu.composemusicplayer.domain.repository.LyricsRepository
import com.sdu.composemusicplayer.core.database.model.LyricsResult
import com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource
import com.sdu.composemusicplayer.core.model.lyrics.PlainLyrics
import com.sdu.composemusicplayer.core.model.lyrics.SynchronizedLyrics
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.network.data.NetworkMonitor
import com.sdu.composemusicplayer.network.model.NetworkStatus
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.constructStringForSaving
import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// LiveLyricsScreen에서 직접적으로 사용할 UI 상태
data class LiveLyricsUiState(
    val currentPlayedMusic: Music? = null,
    val lyricsScreenState: LyricsScreenState = LyricsScreenState.Loading,
    val isPlaying: Boolean = false,
    val currentProgress: Float = 0f, // 0.0f ~ 1.0f
    val currentTimeDisplay: String = "0:00",
    val totalTimeDisplay: String = "0:00",
    val lyricsSourceForMenu: LyricsFetchSource? = null, // "Save to file" 메뉴 표시 여부 결정용
    val lyricsTextForCopy: String = "", // 복사할 전체 가사 텍스트
)

@HiltViewModel
class LiveLyricsViewModel
    @Inject
    constructor(
        private val playerEnvironment: IPlayerEnvironment,
        private val lyricsRepository: LyricsRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() { // StatefulViewModel 대신 표준 ViewModel 사용 또는 StatefulViewModel 유지 선택

        private val _lyricsScreenStateInternal = MutableStateFlow<LyricsScreenState>(LyricsScreenState.Loading)

        // 현재 재생 곡, 재생 상태, 시간 정보 등을 playerEnvironment에서 가져와 결합
        @OptIn(ExperimentalCoroutinesApi::class)
        private val playerDependentState: StateFlow<PlayerDependentData> =
            playerEnvironment
                .getCurrentPlayedMusic()
                .flatMapLatest { music ->
                    if (music == Music.default) {
                        flowOf(PlayerDependentData(currentPlayedMusic = Music.default))
                    } else {
                        combine(
                            playerEnvironment.isPlaying(),
                            playerEnvironment.getCurrentDuration(),
                        ) { isPlaying, currentDurationMs ->
                            PlayerDependentData(
                                currentPlayedMusic = music,
                                isPlaying = isPlaying,
                                currentDurationMs = currentDurationMs,
                                totalDurationMs = music.duration,
                            )
                        }
                    }
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(AppConstants.LYRICS_CACHE_DURATION_MS),
                    initialValue = PlayerDependentData(),
                )

        val uiState: StateFlow<LiveLyricsUiState> =
            combine(
                playerDependentState,
                _lyricsScreenStateInternal,
            ) { playerData, lyricsState ->
                val progress =
                    if (playerData.totalDurationMs > 0) {
                        playerData.currentDurationMs.toFloat() / playerData.totalDurationMs
                    } else {
                        0f
                    }

                val lyricsTextToCopy =
                    when (lyricsState) {
                        is LyricsScreenState.SyncedLyrics -> lyricsState.syncedLyrics.constructStringForSharing()
                        is LyricsScreenState.TextLyrics -> lyricsState.plainLyrics.lines.joinToString("\n")
                        else -> ""
                    }
                val lyricsSourceForMenu =
                    when (lyricsState) {
                        is LyricsScreenState.SyncedLyrics -> lyricsState.lyricsSource
                        is LyricsScreenState.TextLyrics -> lyricsState.lyricsSource
                        else -> null
                    }

                LiveLyricsUiState(
                    currentPlayedMusic = playerData.currentPlayedMusic,
                    lyricsScreenState = lyricsState,
                    isPlaying = playerData.isPlaying,
                    currentProgress = progress.coerceIn(0f, 1f),
                    currentTimeDisplay = formatMillis(playerData.currentDurationMs),
                    totalTimeDisplay = formatMillis(playerData.totalDurationMs),
                    lyricsSourceForMenu = lyricsSourceForMenu,
                    lyricsTextForCopy = lyricsTextToCopy,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AppConstants.LYRICS_CACHE_DURATION_MS),
                initialValue = LiveLyricsUiState(),
            )

        init {
            // 현재 곡 변경 시 가사 로드
            viewModelScope.launch {
                playerDependentState
                    .map { it.currentPlayedMusic }
                    .distinctUntilChanged()
                    .collect { music ->
                        if (music != null && music != Music.default) {
                            loadLyrics(music)
                        } else {
                            _lyricsScreenStateInternal.value = LyricsScreenState.NotPlaying
                        }
                    }
            }

            // 네트워크 상태 변경 시 재시도 로직
            viewModelScope.launch {
                networkMonitor.state.collect { networkStatus ->
                    if (networkStatus == NetworkStatus.CONNECTED) {
                        val currentLyricsState = _lyricsScreenStateInternal.value
                        if (currentLyricsState is LyricsScreenState.NoLyrics &&
                            currentLyricsState.reason == NoLyricsReason.NETWORK_ERROR
                        ) {
                            retryLoadLyrics()
                        }
                    }
                }
            }
        }

        private suspend fun loadLyrics(song: Music) {
            // 기존 loadLyrics 로직과 유사하게 구현
            // withContext(Dispatchers.IO) { ... }
            // _lyricsScreenStateInternal.value = ...
            withContext(Dispatchers.IO) {
                if (song == Music.default) {
                    _lyricsScreenStateInternal.value = LyricsScreenState.NotPlaying
                    return@withContext
                }
                _lyricsScreenStateInternal.value = LyricsScreenState.SearchingLyrics

                val cleanedArtist = song.artist.replace(Regex("\\[.*?\\]"), "").trim()
                val lyricsResult =
                    lyricsRepository
                        .getLyrics(
                            uri = song.audioPath.toUri(),
                            song.title,
                            song.albumPath,
                            cleanedArtist,
                            (song.duration / AppConstants.LYRICS_UPDATE_INTERVAL_MS).toInt(),
                        )

                val newState =
                    when (lyricsResult) {
                        is LyricsResult.NotFound ->
                            LyricsScreenState.NoLyrics(NoLyricsReason.NOT_FOUND)

                        is LyricsResult.NetworkError ->
                            LyricsScreenState.NoLyrics(NoLyricsReason.NETWORK_ERROR)

                        is LyricsResult.FoundPlainLyrics ->
                            LyricsScreenState.TextLyrics(lyricsResult.plainLyrics, lyricsResult.lyricsSource)

                        is LyricsResult.FoundSyncedLyrics ->
                            LyricsScreenState.SyncedLyrics(lyricsResult.syncedLyrics, lyricsResult.lyricsSource)
                    }

                if (isActive) {
                    _lyricsScreenStateInternal.value = newState
                }
            }
        }

        fun retryLoadLyrics() {
            val currentMusic = playerDependentState.value.currentPlayedMusic
            if (currentMusic != null && currentMusic != Music.default) {
                viewModelScope.launch {
                    loadLyrics(currentMusic)
                }
            }
        }

        fun togglePlayPause() {
            viewModelScope.launch {
                if (uiState.value.isPlaying == false) {
                    playerEnvironment.resume()
                } else {
                    playerEnvironment.pause()
                }
            }
        }

        fun seekToProgress(progress: Float) {
            val totalDuration = playerDependentState.value.totalDurationMs
            if (totalDuration > 0) {
                val newPosition = (totalDuration * progress).toLong()
                playerEnvironment.snapTo(newPosition, true)
            }
        }

        fun setSongProgressMillis(millis: Long) { // SyncedLyricsState에서 직접 호출
            playerEnvironment.snapTo(millis, true)
        }

        fun saveExternalLyricsToSongFile() {
            viewModelScope.launch(Dispatchers.IO) {
                val currentLyricsState = _lyricsScreenStateInternal.value
                val currentMusic = playerDependentState.value.currentPlayedMusic

                if (currentMusic != null && currentMusic != Music.default) {
                    when (currentLyricsState) {
                            is LyricsScreenState.TextLyrics -> {
                                if (currentLyricsState.lyricsSource == LyricsFetchSource.EXTERNAL) {
//                                    lyricsRepository.saveLyricsToFile(
//                                        currentMusic.audioPath.toUri(),
//                                        currentLyricsState.plainLyrics.constructStringForSaving(), 
//                                        // 모델에 저장용 문자열 변환 함수 필요
//                                    )
                                } else {
                                    false
                                }
                            }

                            is LyricsScreenState.SyncedLyrics -> {
                                if (currentLyricsState.lyricsSource == LyricsFetchSource.EXTERNAL) {
//                                    lyricsRepository.saveLyricsToFile(
//                                        currentMusic.audioPath.toUri(),
//                                        currentLyricsState.syncedLyrics.constructStringForSaving(), 
//                                        // 모델에 저장용 문자열 변환 함수 필요 (LRC)
//                                    )
                                } else {
                                    false
                                }
                            }

                            else -> false
                        }
                    
                }
            }
        }

        // LiveLyricsScreen의 songProgressMillis() 대신 currentDurationMs를 직접 사용하거나,
        // 필요하다면 이 함수를 유지할 수도 있습니다.
        // 하지만 uiState.currentTimeDisplay 또는 uiState.currentProgress로 대체하는 것이 좋습니다.
        fun getCurrentSongProgressMillis(): Long {
            return playerDependentState.value.currentDurationMs
        }

        private fun formatMillis(millis: Long): String {
            if (millis < 0) return "0:00" // 혹은 에러 표시
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(minutes)
            return String.format("%d:%02d", minutes, seconds)
        }
    }

// playerEnvironment에서 오는 데이터를 묶기 위한 내부 데이터 클래스
private data class PlayerDependentData(
    val currentPlayedMusic: Music? = null,
    val isPlaying: Boolean = false,
    val currentDurationMs: Long = 0L,
    val totalDurationMs: Long = 0L,
)

// SynchronizedLyrics 및 PlainLyrics에 추가될 수 있는 확장 함수 (예시)
fun SynchronizedLyrics.constructStringForSharing(): String {
    return this.segments.joinToString("\n") { it.text }
}

fun PlainLyrics.constructStringForSaving(): String {
    return this.lines.joinToString("\n")
}

fun SynchronizedLyrics.constructStringForSaving(): String {
    // LRC 형식으로 변환하는 로직 구현
    // 예: return segments.joinToString("\n") { "[${formatMillisToLrcTime(it.durationMillis)}]${it.text}" }
    // formatMillisToLrcTime 함수는 LRC 시간 형식 (mm:ss.xx)으로 변환해야 함
    return this.segments.joinToString("\n") { 
        "[${formatMillisToLrcTime(it.durationMillis.toLong())}]${it.text}" 
    } // 예시, 실제 구현 필요
}

private fun formatMillisToLrcTime(millis: Long): String {
    if (millis < 0) return "00:00.00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    val hundredths = (TimeUnit.MILLISECONDS.toMillis(millis) - 
        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))) / AppConstants.LYRICS_UPDATE_INTERVAL_MS
    return String.format("%02d:%02d.%02d", minutes, seconds, hundredths)
}
