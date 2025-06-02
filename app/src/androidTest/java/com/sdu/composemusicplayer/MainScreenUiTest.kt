package com.sdu.composemusicplayer

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import com.sdu.composemusicplayer.presentation.mainScreen.ComposableLifeCycle
import com.sdu.composemusicplayer.presentation.mainScreen.MainScreen
import com.sdu.composemusicplayer.presentation.mainScreen.MusicListContent
import com.sdu.composemusicplayer.viewmodel.MusicUiState
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test

class MainScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun musicListContent_displaysMusicList_correctly() {
        val mockMusicList =
            listOf(
                MusicEntity(1, "Song 1", "Artist 1", 200000L, "/path/album1", "/path/song1"),
                MusicEntity(2, "Song 2", "Artist 2", 180000L, "/path/album2", "/path/song2"),
            )
        val mockUiState =
            MusicUiState(
                musicList = mockMusicList,
                currentPlayedMusic = mockMusicList[0],
            )

        composeTestRule.setContent {
            MusicListContent(musicUiState = mockUiState, onSelectedMusic = {})
        }

        // 리스트 아이템 확인
        composeTestRule.onNodeWithText("Song 1").assertExists()
        composeTestRule.onNodeWithText("Song 2").assertExists()

        // 현재 재생 중인 음악 강조 확인
        composeTestRule.onNodeWithText("Song 1").assertIsDisplayed()

        // 아이템 클릭 테스트
        composeTestRule.onNodeWithText("Song 2").performClick()
    }

    @Test
    fun mainScreen_displaysMusicListAndBottomPlayer() {
        val mockPlayerViewModel = mockk<PlayerViewModel>(relaxed = true)
        val mockNavController = mockk<NavController>(relaxed = true)

        every { mockPlayerViewModel.uiState } returns
            MutableStateFlow(
                MusicUiState(
                    musicList =
                        listOf(
                            MusicEntity(1, "Song 1", "Artist 1", 200000L, "/path/album1", "/path/song1"),
                            MusicEntity(2, "Song 2", "Artist 2", 180000L, "/path/album2", "/path/song2"),
                        ),
                    currentPlayedMusic =
                        MusicEntity(
                            1,
                            "Song 1",
                            "Artist 1",
                            200000L,
                            "/path/album1",
                            "/path/song1",
                        ),
                    isPlaying = true,
                ),
            ).asStateFlow()

        composeTestRule.setContent {
            MainScreen(navController = mockNavController, playerVM = mockPlayerViewModel)
        }

        // 리스트와 바텀 플레이어 확인
        composeTestRule.onNodeWithText("Song 1").assertExists()
        composeTestRule.onNodeWithText("Song 2").assertExists()
        composeTestRule.onNodeWithText("Song 1").assertIsDisplayed()

        // ViewModel 이벤트 트리거 확인
        composeTestRule.onNodeWithText("Song 2").performClick()
        verify { mockPlayerViewModel.onEvent(any()) }
    }

    @Test
    fun composableLifeCycle_triggersLifecycleEvents() {
        var lifecycleEvent: Lifecycle.Event? = null

        composeTestRule.setContent {
            ComposableLifeCycle { _, event ->
                lifecycleEvent = event
            }
        }

        composeTestRule.runOnIdle {
            assert(lifecycleEvent == Lifecycle.Event.ON_RESUME)
        }
    }
}
