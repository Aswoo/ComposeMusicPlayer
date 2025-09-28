import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource
import com.sdu.composemusicplayer.core.model.lyrics.PlainLyrics
import com.sdu.composemusicplayer.core.model.lyrics.SyncedLyricsSegment
import com.sdu.composemusicplayer.core.model.lyrics.SynchronizedLyrics
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics.*
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LiveLyricsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @RelaxedMockK
    private lateinit var mockViewModel: LiveLyricsViewModel

    private lateinit var uiStateFlow: MutableStateFlow<LiveLyricsUiState>

    private val testMusic = Music(audioId = 1, title = "행복", artist = "H.O.T.", duration = 200000L, audioPath = "", albumPath = "")
    private val defaultUiState = LiveLyricsUiState() // 모든 필드가 기본값인 상태

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        uiStateFlow = MutableStateFlow(defaultUiState)
        every { mockViewModel.uiState } returns uiStateFlow
        every { mockViewModel.getCurrentSongProgressMillis() } returns 0L
    }

    private fun setScreenContent(
        onSwap: () -> Unit = {},
        onBack: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            LiveLyricsScreen(
                onSwap = onSwap,
                onBack = onBack,
                lyricsViewModel = mockViewModel,
            )
        }
    }

    // --- 1. 초기 상태 및 UI 요소 표시 테스트 ---

    @Test
    fun `로딩_상태일_때_로딩_인디케이터가_표시된다`() {
        // Arrange
        uiStateFlow.value = defaultUiState.copy(lyricsScreenState = LyricsScreenState.Loading)
        // Act
        setScreenContent()
        // Assert
        composeTestRule.onNodeWithTag("LoadingIndicator", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `재생중인_곡이_없을_때_안내_메시지가_표시된다`() {
        // Arrange
        uiStateFlow.value =
            defaultUiState.copy(
                currentPlayedMusic = null, // 명시적으로 null 또는 Music.NONE (ViewModel 구현에 따라)
                lyricsScreenState = LyricsScreenState.NotPlaying,
            )
        // Act
        setScreenContent()
        // Assert
        composeTestRule.onNodeWithText("No song is being played.").assertIsDisplayed()
    }

    @Test
    fun `곡_정보가_있을_때_상단바에_제목과_아티스트가_표시된다`() {
        // Arrange
        uiStateFlow.value = defaultUiState.copy(currentPlayedMusic = testMusic)
        // Act
        setScreenContent()
        // Assert
        composeTestRule.onNodeWithText(testMusic.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(testMusic.artist).assertIsDisplayed()
    }

    // --- 2. 가사 없음 (NoLyricsState) 상태 테스트 ---

    @Test
    fun `가사를_찾을_수_없을_때_안내_메시지와_뒤로가기_버튼이_표시되고_버튼_클릭시_onBack이_호출된다`() {
        // Arrange
        val mockOnBack: () -> Unit = mockk(relaxed = true)
        uiStateFlow.value = defaultUiState.copy(lyricsScreenState = LyricsScreenState.NoLyrics(NoLyricsReason.NOT_FOUND))
        // Act
        setScreenContent(onBack = mockOnBack)
        // Assert
        composeTestRule.onNodeWithText("No lyrics available").assertIsDisplayed()
        composeTestRule.onNodeWithText("Go Back").assertIsDisplayed().performClick()
        verify { mockOnBack() }
    }

    @Test
    fun `네트워크_오류로_가사_없을_때_안내_메시지와_재시도_버튼이_표시되고_버튼_클릭시_ViewModel의_retryLoadLyrics가_호출된다`() {
        // Arrange
        uiStateFlow.value = defaultUiState.copy(lyricsScreenState = LyricsScreenState.NoLyrics(NoLyricsReason.NETWORK_ERROR))
        every { mockViewModel.retryLoadLyrics() } just Runs
        // Act
        setScreenContent()
        composeTestRule.onNodeWithText("Try Again").performClick()
        // Assert
        composeTestRule.onNodeWithText("Check your network connection").assertIsDisplayed()
        verify { mockViewModel.retryLoadLyrics() }
    }

    // --- 3. 일반 텍스트 가사 (PlainLyricsState) 표시 테스트 ---

    @Test
    fun `일반_텍스트_가사가_있을_때_모든_가사_라인이_표시된다`() {
        // Arrange
        val lyricsLines = listOf("첫 번째 가사 라인", "두 번째 가사 라인입니다")
        val plainLyrics = PlainLyrics(lyricsLines)
        val textLyricsState = LyricsScreenState.TextLyrics(plainLyrics, LyricsFetchSource.FROM_SONG_METADATA)
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsScreenState = textLyricsState,
                lyricsTextForCopy = lyricsLines.joinToString("\n"), // 복사할 텍스트도 설정
                lyricsSourceForMenu = LyricsFetchSource.FROM_SONG_METADATA, // 메뉴 표시 여부 결정용
            )
        // Act
        setScreenContent()
        // Assert
        lyricsLines.forEach { line ->
            composeTestRule.onNodeWithText(line).assertIsDisplayed()
        }
    }

    // --- 4. 동기화된 가사 (SyncedLyricsState) 표시 및 동작 테스트 ---

    @Test
    fun `동기화된_가사가_있을_때_모든_세그먼트가_표시된다`() {
        // Arrange
        val segments =
            listOf(
                SyncedLyricsSegment("시간 동기화 가사 1", 0),
                SyncedLyricsSegment("시간 동기화 가사 2", 3000),
            )
        val syncedLyrics = SynchronizedLyrics(segments)
        val syncedLyricsState = LyricsScreenState.SyncedLyrics(syncedLyrics, LyricsFetchSource.EXTERNAL)
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsScreenState = syncedLyricsState,
                lyricsTextForCopy = segments.joinToString("\n") { it.text },
                lyricsSourceForMenu = LyricsFetchSource.EXTERNAL,
            )
        // Act
        setScreenContent()
        // Assert
        segments.forEach { segment ->
            composeTestRule.onNodeWithText(segment.text).assertIsDisplayed()
        }
    }

    @Test
    fun `동기화된_가사_라인_클릭_시_ViewModel의_setSongProgressMillis가_호출된다`() {
        // Arrange
        val targetTimeMillis = 5500
        val segments =
            listOf(
                SyncedLyricsSegment("처음", 0),
                SyncedLyricsSegment("클릭할 라인", targetTimeMillis),
            )
        val syncedLyrics = SynchronizedLyrics(segments)
        val syncedLyricsState = LyricsScreenState.SyncedLyrics(syncedLyrics, LyricsFetchSource.FROM_SONG_METADATA)
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsScreenState = syncedLyricsState,
                lyricsSourceForMenu = LyricsFetchSource.FROM_SONG_METADATA,
            )
        every { mockViewModel.setSongProgressMillis(any()) } just Runs
        // Act
        setScreenContent()
        composeTestRule.onNodeWithText("클릭할 라인").performClick()
        // Assert
        verify { mockViewModel.setSongProgressMillis(targetTimeMillis.toLong()) }
    }

    // --- 5. 하단 컨트롤러 (LyricsScreenBottomControls) 테스트 ---

    @Test
    fun `재생_중일_때_일시정지_버튼이_표시되고_클릭_시_ViewModel의_togglePlayPause가_호출된다`() {
        // Arrange
        val plainLyrics = PlainLyrics(emptyList())
        val textLyricsState = LyricsScreenState.TextLyrics(plainLyrics, LyricsFetchSource.FROM_SONG_METADATA)
        uiStateFlow.value =
            defaultUiState.copy(
                isPlaying = true,
                lyricsScreenState = textLyricsState, // 컨트롤러 표시를 위해 가사 상태도 설정
                lyricsSourceForMenu = LyricsFetchSource.FROM_SONG_METADATA,
            )
        every { mockViewModel.togglePlayPause() } just Runs
        // Act
        setScreenContent()
        composeTestRule.onNodeWithContentDescription("Pause").performClick()
        // Assert
        composeTestRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
        verify { mockViewModel.togglePlayPause() }
    }

    @Test
    fun `일시정지_중일_때_재생_버튼이_표시되고_클릭_시_ViewModel의_togglePlayPause가_호출된다`() {
        // Arrange
        val plainLyrics = PlainLyrics(emptyList())
        val textLyricsState = LyricsScreenState.TextLyrics(plainLyrics, LyricsFetchSource.FROM_SONG_METADATA)
        uiStateFlow.value =
            defaultUiState.copy(
                isPlaying = false,
                lyricsScreenState = textLyricsState,
                lyricsSourceForMenu = LyricsFetchSource.FROM_SONG_METADATA,
            )
        every { mockViewModel.togglePlayPause() } just Runs
        // Act
        setScreenContent()
        composeTestRule.onNodeWithContentDescription("Play").performClick()
        // Assert
        composeTestRule.onNodeWithContentDescription("Play").assertIsDisplayed()
        verify { mockViewModel.togglePlayPause() }
    }

    @Test
    fun `진행률_변경_시_슬라이더에_반영된다`() {
        // Arrange
        val plainLyrics = PlainLyrics(emptyList())
        val textLyricsState = LyricsScreenState.TextLyrics(plainLyrics, LyricsFetchSource.FROM_SONG_METADATA)
        uiStateFlow.value =
            defaultUiState.copy(
                currentProgress = 0.6f,
                lyricsScreenState = textLyricsState,
                lyricsSourceForMenu = LyricsFetchSource.FROM_SONG_METADATA,
            )
        // Act
        setScreenContent()
        // Assert - 진행률이 슬라이더에 반영되는지 확인
        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(current = 0.6f, range = 0f..1f, steps = 0)))
            .assertIsDisplayed()
    }

    @Test
    fun `시간_정보_변경_시_화면에_올바르게_표시된다`() {
        // Arrange
        val currentTime = "0:45"
        val totalTime = "3:30"
        val plainLyrics = PlainLyrics(emptyList())
        val textLyricsState = LyricsScreenState.TextLyrics(plainLyrics, LyricsFetchSource.FROM_SONG_METADATA)
        uiStateFlow.value =
            defaultUiState.copy(
                currentTimeDisplay = currentTime,
                totalTimeDisplay = totalTime,
                lyricsScreenState = textLyricsState,
                lyricsSourceForMenu = LyricsFetchSource.FROM_SONG_METADATA,
            )
        // Act
        setScreenContent()
        // Assert
        composeTestRule.onNodeWithText(currentTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(totalTime).assertIsDisplayed()
    }

    @Test
    fun `외부_가사이고_더보기_클릭_시_저장_버튼이_표시되고_클릭_시_ViewModel의_saveExternalLyricsToSongFile이_호출된다`() {
        // Arrange
        val plainLyrics = PlainLyrics(listOf("외부 가사"))
        val textLyricsState = LyricsScreenState.TextLyrics(plainLyrics, LyricsFetchSource.EXTERNAL)
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsSourceForMenu = LyricsFetchSource.EXTERNAL,
                lyricsScreenState = textLyricsState,
            )
        every { mockViewModel.saveExternalLyricsToSongFile() } just Runs
        // Act
        setScreenContent()
        composeTestRule.onNodeWithContentDescription("More Actions").performClick()
        composeTestRule.onNodeWithText("Save to Song File").performClick()
        // Assert
        verify { mockViewModel.saveExternalLyricsToSongFile() }
    }

    @Test
    fun `내부_가사이고_더보기_클릭_시_저장_버튼이_표시되지_않는다`() {
        // Arrange
        val plainLyrics = PlainLyrics(listOf("내부 가사"))
        val textLyricsState = LyricsScreenState.TextLyrics(plainLyrics, LyricsFetchSource.FROM_SONG_METADATA)
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsSourceForMenu = LyricsFetchSource.FROM_SONG_METADATA,
                lyricsScreenState = textLyricsState,
            )
        // Act
        setScreenContent()
        composeTestRule.onNodeWithContentDescription("More Actions").performClick()
        // Assert
        composeTestRule.onNodeWithText("Save to Song File").assertDoesNotExist()
    }

    // --- 6. 상단 바 (LyricsScreenTopAppBar) 테스트 ---

    @Test
    fun `상단바의_뒤로가기_버튼_클릭_시_onBack_콜백이_호출된다`() {
        // Arrange
        val mockOnBack: () -> Unit = mockk(relaxed = true)
        uiStateFlow.value = defaultUiState // 어떤 특정 상태가 아니어도 됨
        // Act
        setScreenContent(onBack = mockOnBack)
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        // Assert
        verify { mockOnBack() }
    }

    @Test
    fun `상단바의_가사소스변경_버튼_클릭_시_onSwap_콜백이_호출된다`() {
        // Arrange
        val mockOnSwap: () -> Unit = mockk(relaxed = true)
        uiStateFlow.value = defaultUiState
        // Act
        setScreenContent(onSwap = mockOnSwap)
        composeTestRule.onNodeWithContentDescription("Swap Lyrics Source").performClick()
        // Assert
        verify { mockOnSwap() }
    }

    @Test
    fun `네트워크_오류_상태일_때_재시도_버튼이_표시된다`() {
        // Arrange
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsScreenState = LyricsScreenState.NoLyrics(NoLyricsReason.NETWORK_ERROR),
            )
        val mockOnRetry = mockk<() -> Unit>(relaxed = true)

        // Act
        composeTestRule.setContent {
            LiveLyricsContent(
                modifier = androidx.compose.ui.Modifier,
                lyricsScreenState = uiStateFlow.value.lyricsScreenState,
                songProgressMillis = { 0L },
                onSeekToPositionMillis = {},
                onRetry = mockOnRetry,
                onBack = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Check your network connection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }

    @Test
    fun `가사가_없을_때_안내_메시지가_표시된다`() {
        // Arrange
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsScreenState = LyricsScreenState.NoLyrics(NoLyricsReason.NOT_FOUND),
            )
        val mockOnBack = mockk<() -> Unit>(relaxed = true)

        // Act
        composeTestRule.setContent {
            LiveLyricsContent(
                modifier = androidx.compose.ui.Modifier,
                lyricsScreenState = uiStateFlow.value.lyricsScreenState,
                songProgressMillis = { 0L },
                onSeekToPositionMillis = {},
                onRetry = {},
                onBack = mockOnBack,
            )
        }

        // Assert
        composeTestRule.onNodeWithText("No lyrics available").assertIsDisplayed()
        composeTestRule.onNodeWithText("Go Back").assertIsDisplayed()
    }

    @Test
    fun `가사_검색_상태일_때_검색_중_메시지가_표시된다`() {
        // Arrange
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsScreenState = LyricsScreenState.SearchingLyrics,
            )

        // Act
        composeTestRule.setContent {
            LiveLyricsContent(
                modifier = androidx.compose.ui.Modifier,
                lyricsScreenState = uiStateFlow.value.lyricsScreenState,
                songProgressMillis = { 0L },
                onSeekToPositionMillis = {},
                onRetry = {},
                onBack = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("LoadingIndicator", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `동기화된_가사에서_현재_진행_시간이_올바르게_표시된다`() {
        // Arrange
        val syncedLyrics =
            SynchronizedLyrics(
                listOf(
                    SyncedLyricsSegment("첫 번째 가사", 0),
                    SyncedLyricsSegment("두 번째 가사", 5000),
                    SyncedLyricsSegment("세 번째 가사", 10000),
                ),
            )
        uiStateFlow.value =
            defaultUiState.copy(
                lyricsScreenState = LyricsScreenState.SyncedLyrics(syncedLyrics, LyricsFetchSource.FROM_SONG_METADATA),
                currentProgress = 0.5f,
            )

        // Act
        composeTestRule.setContent {
            LiveLyricsContent(
                modifier = androidx.compose.ui.Modifier,
                lyricsScreenState = uiStateFlow.value.lyricsScreenState,
                songProgressMillis = { 5000L },
                onSeekToPositionMillis = {},
                onRetry = {},
                onBack = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithText("첫 번째 가사").assertIsDisplayed()
        composeTestRule.onNodeWithText("두 번째 가사").assertIsDisplayed()
        composeTestRule.onNodeWithText("세 번째 가사").assertIsDisplayed()
    }
}
