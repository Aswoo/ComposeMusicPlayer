package com.sdu.composemusicplayer.presentation.playlists.playlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import androidx.compose.ui.Modifier
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class PlaylistsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `플레이리스트_목록이_정상적으로_표시된다`() {
        // Arrange
        val playlists = listOf(
            PlaylistInfo(1, "Playlist 1", 10),
            PlaylistInfo(2, "Playlist 2", 5)
        )
        val state = PlaylistsScreenState.Success(playlists)

        // Act
        composeTestRule.setContent {
            PlaylistsScreen(
                modifier = Modifier,
                state = state,
                onPlaylistClicked = {},
                onDeletePlaylist = {},
                onRenamePlaylist = { _, _ -> }
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Playlist 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Playlist 2").assertIsDisplayed()
    }

    @Test
    fun `플레이리스트가_비어있을_때_빈_화면이_표시된다`() {
        // Arrange
        val state = PlaylistsScreenState.Success(emptyList())

        // Act
        composeTestRule.setContent {
            PlaylistsScreen(
                modifier = Modifier,
                state = state,
                onPlaylistClicked = {},
                onDeletePlaylist = {},
                onRenamePlaylist = { _, _ -> }
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Playlists").assertIsDisplayed()
    }

    @Test
    fun `플레이리스트_항목_클릭_시_onPlaylistClicked가_호출된다`() {
        // Arrange
        val onPlaylistClickedMock = mockk<(Int) -> Unit>(relaxed = true)
        val playlists = listOf(PlaylistInfo(1, "Playlist 1", 10))
        val state = PlaylistsScreenState.Success(playlists)

        composeTestRule.setContent {
            PlaylistsScreen(
                modifier = Modifier,
                state = state,
                onPlaylistClicked = onPlaylistClickedMock,
                onDeletePlaylist = {},
                onRenamePlaylist = { _, _ -> }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Playlist 1").performClick()

        // Assert
        verify { onPlaylistClickedMock(1) }
    }
}
