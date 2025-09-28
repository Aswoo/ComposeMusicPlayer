package com.sdu.composemusicplayer.presentation.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.SortOption
import com.sdu.composemusicplayer.domain.model.SortState
import com.sdu.composemusicplayer.presentation.player.MusicUiState
import com.sdu.composemusicplayer.presentation.player.PlayerViewModel
import com.sdu.composemusicplayer.ui.theme.SpotiBackground
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MusicScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockPlayerViewModel: PlayerViewModel
    private lateinit var mockNavController: NavController

    private val mockMusicList =
        listOf(
            Music(
                audioId = 1,
                title = "Song 1",
                artist = "Artist 1",
                duration = 200000L,
                albumPath = "/path/album1",
                audioPath = "/path/song1",
            ),
            Music(
                audioId = 2,
                title = "Song 2",
                artist = "Artist 2",
                duration = 180000L,
                albumPath = "/path/album2",
                audioPath = "/path/song2",
            ),
        )

    @Before
    fun setUp() {
        mockPlayerViewModel = mockk(relaxed = true)
        mockNavController = mockk(relaxed = true)
    }

    @Composable
    fun TestableMusicScreen(
        playerVM: PlayerViewModel,
        onSelectedMusic: (Music) -> Unit,
        updateSortState: (SortState) -> Unit,
    ) {
        val musicUiState = playerVM.uiState.value

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(SpotiBackground),
        ) {
            MusicListContent(
                musicUiState = musicUiState,
                onSelectedMusic = onSelectedMusic,
                updateSortState = updateSortState,
            )
        }
    }

    @Test
    fun `음악_목록이_정상적으로_표시된다`() {
        // Arrange
        val uiState =
            MusicUiState(
                musicList = mockMusicList,
                sortState = SortState(),
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(uiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestableMusicScreen(
                    playerVM = mockPlayerViewModel,
                    onSelectedMusic = {},
                    updateSortState = {},
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("마이 라이브러리").assertIsDisplayed()
        composeTestRule.onNodeWithText("TITLE").assertIsDisplayed()
        composeTestRule.onNodeWithText("ARTIST").assertIsDisplayed()
        composeTestRule.onNodeWithText("Song 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artist 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Song 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artist 2").assertIsDisplayed()
    }

    @Test
    fun `음악_목록이_비어있을_때_빈_메시지가_표시된다`() {
        // Arrange
        val uiState =
            MusicUiState(
                musicList = emptyList(),
                sortState = SortState(),
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(uiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestableMusicScreen(
                    playerVM = mockPlayerViewModel,
                    onSelectedMusic = {},
                    updateSortState = {},
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("음악을 찾을 수 없습니다.").assertIsDisplayed()
    }

    @Test
    fun `음악_항목_클릭_시_onSelectedMusic이_호출된다`() {
        // Arrange
        val onSelectedMusicMock = mockk<(Music) -> Unit>(relaxed = true)
        val uiState =
            MusicUiState(
                musicList = mockMusicList,
                sortState = SortState(),
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(uiState)

        composeTestRule.setContent {
            MaterialTheme {
                TestableMusicScreen(
                    playerVM = mockPlayerViewModel,
                    onSelectedMusic = onSelectedMusicMock,
                    updateSortState = {},
                )
            }
        }

        // Act
        composeTestRule.onNodeWithText("Song 1").performClick()

        // Assert
        verify { onSelectedMusicMock(mockMusicList[0]) }
    }

    @Test
    fun `정렬_헤더_클릭_시_updateSortState가_호출된다`() {
        // Arrange
        val updateSortStateMock = mockk<(SortState) -> Unit>(relaxed = true)
        val currentSortState = SortState(option = SortOption.ARTIST)
        val uiState =
            MusicUiState(
                musicList = mockMusicList,
                sortState = currentSortState,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(uiState)

        composeTestRule.setContent {
            MaterialTheme {
                TestableMusicScreen(
                    playerVM = mockPlayerViewModel,
                    onSelectedMusic = {},
                    updateSortState = updateSortStateMock,
                )
            }
        }

        // Act
        composeTestRule.onNodeWithText("TITLE").performClick()

        // Assert
        verify { updateSortStateMock(currentSortState.copy(option = SortOption.TITLE)) }
    }

    @Test
    fun `음악_목록이_비어있을_때_빈_상태가_표시된다`() {
        // Arrange
        val uiState =
            MusicUiState(
                musicList = emptyList(),
                sortState = SortState(),
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(uiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestableMusicScreen(
                    playerVM = mockPlayerViewModel,
                    onSelectedMusic = {},
                    updateSortState = {},
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("마이 라이브러리").assertIsDisplayed()
        composeTestRule.onNodeWithText("TITLE").assertIsDisplayed()
        composeTestRule.onNodeWithText("ARTIST").assertIsDisplayed()
    }

    @Test
    fun `음악_아이템을_클릭하면_onSelectedMusic이_호출된다`() {
        // Arrange
        val uiState =
            MusicUiState(
                musicList = mockMusicList,
                sortState = SortState(),
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(uiState)
        var selectedMusic: Music? = null
        val onSelectedMusic: (Music) -> Unit = { music -> selectedMusic = music }

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestableMusicScreen(
                    playerVM = mockPlayerViewModel,
                    onSelectedMusic = onSelectedMusic,
                    updateSortState = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Song 1").performClick()

        // Assert
        assert(selectedMusic != null)
        assert(selectedMusic?.title == "Song 1")
    }

    @Test
    fun `현재_재생중인_음악이_올바르게_표시된다`() {
        // Arrange
        val currentMusic = mockMusicList.first()
        val uiState =
            MusicUiState(
                musicList = mockMusicList,
                sortState = SortState(),
                currentPlayedMusic = currentMusic,
                isPlaying = true,
            )
        every { mockPlayerViewModel.uiState } returns MutableStateFlow(uiState)

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                TestableMusicScreen(
                    playerVM = mockPlayerViewModel,
                    onSelectedMusic = {},
                    updateSortState = {},
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Song 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artist 1").assertIsDisplayed()
    }
}

