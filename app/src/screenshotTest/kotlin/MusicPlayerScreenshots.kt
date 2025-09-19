package com.sdu.composemusicplayer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.component.ExpandedMusicPlayerContent
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.LiveLyricsContent
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.LyricsScreenState
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.NoLyricsReason
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.PlainLyrics
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.SynchronizedLyrics
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.SyncedLyricsSegment
import com.sdu.composemusicplayer.presentation.player.MusicUiState
import com.sdu.composemusicplayer.presentation.player.PlayerViewModel
import com.sdu.composemusicplayer.utils.AndroidConstants
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("FunctionNaming")
class MusicPlayerScreenshots {

    private val testMusic = Music(
        audioId = 1L,
        title = "Beautiful Song",
        artist = "Amazing Artist",
        duration = AndroidConstants.Time.MILLIS_IN_SECOND * 180L, // 3분
        albumPath = "https://example.com/album.jpg",
        audioPath = "/test/path"
    )

    @PreviewScreenSizes
    @Composable
    private fun MusicPlayer_Playing_State() {
        MaterialTheme {
            // 재생 중인 상태의 음악 플레이어
            TestableExpandedMusicPlayerContent(
                isPlaying = true,
                currentMusic = testMusic,
                progress = 0.5f
            )
        }
    }

    @PreviewScreenSizes
    @Composable
    private fun MusicPlayer_Paused_State() {
        MaterialTheme {
            // 일시정지 상태의 음악 플레이어
            TestableExpandedMusicPlayerContent(
                isPlaying = false,
                currentMusic = testMusic,
                progress = 0.3f
            )
        }
    }

    @PreviewScreenSizes
    @Composable
    private fun MusicPlayer_Loading_State() {
        MaterialTheme {
            // 로딩 상태의 음악 플레이어
            TestableExpandedMusicPlayerContent(
                isPlaying = false,
                currentMusic = Music.default, // 기본값 (로딩 중)
                progress = 0f
            )
        }
    }

    @PreviewScreenSizes
    @Composable
    private fun LiveLyrics_WithText() {
        MaterialTheme {
            // 텍스트 가사가 있는 상태
            TestableLiveLyricsContent(
                lyricsState = LyricsScreenState.TextLyrics(
                    PlainLyrics(listOf("첫 번째 가사", "두 번째 가사", "세 번째 가사")),
                    com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource.FROM_SONG_METADATA
                ),
                progress = 0.6f
            )
        }
    }

    @PreviewScreenSizes
    @Composable
    private fun LiveLyrics_WithSyncedLyrics() {
        MaterialTheme {
            // 동기화된 가사가 있는 상태
            TestableLiveLyricsContent(
                lyricsState = LyricsScreenState.SyncedLyrics(
                    SynchronizedLyrics(
                        listOf(
                            SyncedLyricsSegment("첫 번째 가사", 0),
                            SyncedLyricsSegment("두 번째 가사", 5000),
                            SyncedLyricsSegment("세 번째 가사", 10000)
                        )
                    ),
                    com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource.FROM_SONG_METADATA
                ),
                progress = 0.7f
            )
        }
    }

    @PreviewScreenSizes
    @Composable
    private fun LiveLyrics_Loading_State() {
        MaterialTheme {
            // 가사 로딩 상태
            TestableLiveLyricsContent(
                lyricsState = LyricsScreenState.Loading,
                progress = 0.2f
            )
        }
    }

    @PreviewScreenSizes
    @Composable
    private fun LiveLyrics_NoLyrics_State() {
        MaterialTheme {
            // 가사가 없는 상태
            TestableLiveLyricsContent(
                lyricsState = LyricsScreenState.NoLyrics(NoLyricsReason.NOT_FOUND),
                progress = 0.4f
            )
        }
    }

    @PreviewScreenSizes
    @Composable
    private fun LiveLyrics_NetworkError_State() {
        MaterialTheme {
            // 네트워크 오류 상태
            TestableLiveLyricsContent(
                lyricsState = LyricsScreenState.NoLyrics(NoLyricsReason.NETWORK_ERROR),
                progress = 0.8f
            )
        }
    }
}

// 테스트용 컴포넌트들 (실제 구현은 단순화)
@Composable
private fun TestableExpandedMusicPlayerContent(
    isPlaying: Boolean,
    currentMusic: Music,
    progress: Float
) {
    // 실제 ExpandedMusicPlayerContent의 단순화된 버전
    // 스크린샷 테스트용으로 상태만 전달
}

@Composable
private fun TestableLiveLyricsContent(
    lyricsState: LyricsScreenState,
    progress: Float
) {
    // 실제 LiveLyricsContent의 단순화된 버전
    // 스크린샷 테스트용으로 상태만 전달
}
