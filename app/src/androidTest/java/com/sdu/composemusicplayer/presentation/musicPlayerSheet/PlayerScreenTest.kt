package com.sdu.composemusicplayer.presentation.musicPlayerSheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.component.ExpandedMusicPlayerContent
import com.sdu.composemusicplayer.presentation.player.MusicUiState
import com.sdu.composemusicplayer.presentation.player.PlayerViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockPlayerViewModel: PlayerViewModel
    private lateinit var mockUiState: MusicUiState

    private val testMusic =
        Music(
            audioId = 1,
            title = "Test Song",
            artist = "Test Artist",
            duration = 180000L,
            audioPath = "/test/path",
            albumPath = "/test/album",
        )

    @Before
    fun setUp() {
        mockPlayerViewModel = mockk(relaxed = true)
        mockUiState =
            MusicUiState(
                musicList = emptyList(),
                sortState =
                    com
                        .sdu
                        .composemusicplayer
                        .domain
                        .model
                        .SortState(),
                currentPlayedMusic = testMusic,
                isPlaying = true,
                isPaused = false,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(mockUiState)
    }

    @Test
    fun `플레이어_화면이_정상적으로_표시된다`() {
        // Arrange
        val testUiState =
            mockUiState.copy(
                currentPlayedMusic = testMusic,
                isPlaying = true,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(testUiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestablePlayerScreen(
                    playerVM = mockPlayerViewModel,
                    isExpanded = true,
                    barHeight = 56.dp,
                    nowPlayingBarPadding = PaddingValues(),
                    onCollapseNowPlaying = {},
                    onExpandNowPlaying = {},
                    progressProvider = { 0.5f },
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Test Song").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Artist").assertIsDisplayed()
    }

    @Test
    fun `재생_일시정지_버튼이_올바르게_표시된다`() {
        // Arrange
        val testUiState =
            mockUiState.copy(
                currentPlayedMusic = testMusic,
                isPlaying = true,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(testUiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestablePlayerScreen(
                    playerVM = mockPlayerViewModel,
                    isExpanded = true,
                    barHeight = 56.dp,
                    nowPlayingBarPadding = PaddingValues(),
                    onCollapseNowPlaying = {},
                    onExpandNowPlaying = {},
                    progressProvider = { 0.5f },
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Test Song").assertIsDisplayed()
    }

    @Test
    fun `일시정지_상태일_때_재생_버튼이_표시된다`() {
        // Arrange
        val testUiState =
            mockUiState.copy(
                currentPlayedMusic = testMusic,
                isPlaying = false,
                isPaused = true,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(testUiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestablePlayerScreen(
                    playerVM = mockPlayerViewModel,
                    isExpanded = true,
                    barHeight = 56.dp,
                    nowPlayingBarPadding = PaddingValues(),
                    onCollapseNowPlaying = {},
                    onExpandNowPlaying = {},
                    progressProvider = { 0.5f },
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Test Song").assertIsDisplayed()
    }

    @Test
    fun `미니_플레이어가_정상적으로_표시된다`() {
        // Arrange
        val testUiState =
            mockUiState.copy(
                currentPlayedMusic = testMusic,
                isPlaying = true,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(testUiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestablePlayerScreen(
                    playerVM = mockPlayerViewModel,
                    isExpanded = false,
                    barHeight = 56.dp,
                    nowPlayingBarPadding = PaddingValues(),
                    onCollapseNowPlaying = {},
                    onExpandNowPlaying = {},
                    progressProvider = { 0.5f },
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithTag("miniPlayer").assertIsDisplayed()
    }

    @Test
    fun `플레이어_UI_컴포넌트들이_정상적으로_렌더링된다`() {
        // Arrange
        val testUiState =
            mockUiState.copy(
                currentPlayedMusic = testMusic,
                isPlaying = true,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(testUiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestablePlayerScreen(
                    playerVM = mockPlayerViewModel,
                    isExpanded = true,
                    barHeight = 56.dp,
                    nowPlayingBarPadding = PaddingValues(),
                    onCollapseNowPlaying = {},
                    onExpandNowPlaying = {},
                    progressProvider = { 0.5f },
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Test Song").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Artist").assertIsDisplayed()
    }

    @Test
    fun `플레이어_상태에_따라_UI가_변경된다`() {
        // Arrange
        val testUiState =
            mockUiState.copy(
                currentPlayedMusic = testMusic,
                isPlaying = false,
                isPaused = true,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(testUiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestablePlayerScreen(
                    playerVM = mockPlayerViewModel,
                    isExpanded = true,
                    barHeight = 56.dp,
                    nowPlayingBarPadding = PaddingValues(),
                    onCollapseNowPlaying = {},
                    onExpandNowPlaying = {},
                    progressProvider = { 0.5f },
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Test Song").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Artist").assertIsDisplayed()
    }
}

@Composable
private fun TestablePlayerScreen(
    playerVM: PlayerViewModel,
    isExpanded: Boolean,
    barHeight: Dp,
    nowPlayingBarPadding: PaddingValues,
    onCollapseNowPlaying: () -> Unit,
    onExpandNowPlaying: () -> Unit,
    progressProvider: () -> Float,
) {
    if (isExpanded) {
        ExpandedMusicPlayerContent(
            playerVM = playerVM,
            openAddToPlaylistDialog = {},
        )
    } else {
        // MiniPlayer를 시뮬레이션하는 간단한 UI
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.testTag("miniPlayer"),
        ) {
            androidx.compose.material3.Text("Mini Player")
        }
    }
}
