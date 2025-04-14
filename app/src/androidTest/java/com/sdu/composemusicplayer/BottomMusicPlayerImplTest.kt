package com.sdu.composemusicplayer

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.sdu.composemusicplayer.data.music.MusicEntity
import com.sdu.composemusicplayer.navigation.Routes
import com.sdu.composemusicplayer.presentation.mainScreen.component.BottomMusicPlayerImpl
import com.sdu.composemusicplayer.viewmodel.MusicUiState
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class) // Compose 테스트를 위한 실험적 API 사용
class BottomMusicPlayerImplTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: NavController
    private lateinit var mockMusicUiState: MusicUiState

    @Before
    fun setup() {
        // Mock NavController 및 MusicUiState 초기화
        mockNavController = mockk(relaxed = true)
        mockMusicUiState =
            MusicUiState(
                currentPlayedMusic = MusicEntity.default,
                currentDuration = 0L,
                isPlaying = false,
                isBottomPlayerShow = true,
                musicList = emptyList(),
            )
    }

    @Test
    fun bottomMusicPlayer_isVisibleWhenPlayerStateIsVisible() {
        composeTestRule.setContent {
            Box {
                BottomMusicPlayerImpl(
                    navController = mockNavController,
                    musicUiState = mockMusicUiState,
                    onPlayPlauseClicked = {},
                )
            }
        }

        // BottomMusicPlayer 컴포넌트가 나타나는지 확인
        composeTestRule
            .onNodeWithTag("BottomMusicPlayer") // BottomMusicPlayer 내부에 tag 추가 필요
            .assertIsDisplayed()
    }

    @Test
    fun bottomMusicPlayer_navigatesToPlayerOnClick() {
        composeTestRule.setContent {
            Box {
                BottomMusicPlayerImpl(
                    navController = mockNavController,
                    musicUiState = mockMusicUiState,
                    onPlayPlauseClicked = {},
                )
            }
        }

        // 클릭 시 NavController의 navigate 호출 여부 확인
        composeTestRule
            .onNodeWithTag("BottomMusicPlayer") // BottomMusicPlayer 내부에 tag 추가 필요
            .performClick()

        verify { mockNavController.navigate(Routes.Player.name) }
    }

    @Test
    fun bottomMusicPlayer_playPauseToggleTriggersCallback() {
        var isPlaying = false

        composeTestRule.setContent {
            Box {
                BottomMusicPlayerImpl(
                    navController = mockNavController,
                    musicUiState = mockMusicUiState,
                    onPlayPlauseClicked = { isPlaying = true },
                )
            }
        }

        // PlayPause 버튼을 클릭한 후 상태가 업데이트되는지 확인
        composeTestRule
            .onNodeWithTag("PlayPauseButton") // PlayPause 버튼 내부에 tag 추가 필요
            .performClick()

        assert(isPlaying)
    }
}
