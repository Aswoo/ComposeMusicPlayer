package com.sdu.composemusicplayer.presentation.playlists.playlistdetail

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import coil.ImageLoader
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.presentation.component.CommonMusicActions
import com.sdu.composemusicplayer.presentation.component.LocalCommonMusicAction
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.LocalEfficientThumbnailImageLoader
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.LocalInefficientThumbnailImageLoader
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class PlaylistDetailScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val mockMusicList =
        listOf(
            Music(1, "Song 1", "Artist 1", 180000, "path1", "path1"),
            Music(2, "Song 2", "Artist 2", 240000, "path2", "path2"),
        )

    private val mockCommonMusicAction =
        CommonMusicActions(
            deleteAction = { _ -> },
            shareAction = { _, _ -> },
        )

    private fun setPlaylistDetailContent(
        state: PlaylistDetailScreenState,
        playlistActions: PlaylistActions = mockk(relaxed = true),
        onSongClicked: (Music) -> Unit = {},
        onBackPressed: () -> Unit = {},
        onEdit: () -> Unit = {},
    ) {
        val context = composeTestRule.activity
        val fakeImageLoader = ImageLoader.Builder(context).build()

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalCommonMusicAction provides mockCommonMusicAction,
                LocalInefficientThumbnailImageLoader provides fakeImageLoader,
                LocalEfficientThumbnailImageLoader provides fakeImageLoader, // 여기 추가!
            ) {
                PlaylistDetailContent(
                    modifier = Modifier,
                    state = state,
                    playlistActions = playlistActions,
                    onSongClicked = onSongClicked,
                )
            }
        }
    }

    @Test
    fun `플레이리스트_상세정보가_정상적으로_표시된다`() {
        val state =
            PlaylistDetailScreenState.Loaded(
                id = 1,
                name = "My Playlist",
                music = mockMusicList,
                currentPlayingMusic = null,
            )

        setPlaylistDetailContent(state)

        composeTestRule.onNodeWithText("My Playlist").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 tracks • 07:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("Song 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Song 2").assertIsDisplayed()
    }

    @Test
    fun `음악_항목_클릭_시_onSongClicked가_호출된다`() {
        val onSongClickedMock = mockk<(Music) -> Unit>(relaxed = true)
        val state =
            PlaylistDetailScreenState.Loaded(
                id = 1,
                name = "My Playlist",
                music = mockMusicList,
                currentPlayingMusic = null,
            )

        setPlaylistDetailContent(state, onSongClicked = onSongClickedMock)

        composeTestRule.onNodeWithText("Song 1").performClick()

        verify { onSongClickedMock(mockMusicList[0]) }
    }

    @Test
    fun `재생_버튼_클릭_시_play_함수가_호출된다`() {
        val playlistActionsMock = mockk<PlaylistActions>(relaxed = true)
        val state =
            PlaylistDetailScreenState.Loaded(
                id = 1,
                name = "My Playlist",
                music = mockMusicList,
                currentPlayingMusic = null,
            )

        setPlaylistDetailContent(state, playlistActions = playlistActionsMock)

        composeTestRule.onNodeWithText("Play").performClick()

        verify { playlistActionsMock.play() }
    }
}
