package com.sdu.composemusicplayer.presentation.playlists.playlist

import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PlaylistsScreenStateTest {
    private val testPlaylistInfo =
        PlaylistInfo(
            id = 1,
            name = "Test Playlist",
            numberOfMusic = 5,
        )

    @Test
    fun `로딩_상태가_올바르게_식별된다`() {
        // Given
        val loadingState = PlaylistsScreenState.Loading

        // Then
        assertTrue("Should be loading state", loadingState is PlaylistsScreenState.Loading)
    }

    @Test
    fun `성공_상태에서_플레이리스트가_올바르게_포함된다`() {
        // Given
        val playlists = listOf(testPlaylistInfo)
        val successState = PlaylistsScreenState.Success(playlists)

        // Then
        assertTrue("Should be success state", successState is PlaylistsScreenState.Success)
        assertEquals("Should contain correct playlists", playlists, successState.playlists)
        assertEquals("Should have correct playlist count", 1, successState.playlists.size)
        assertEquals("Should have correct playlist name", "Test Playlist", successState.playlists[0].name)
    }

    @Test
    fun `성공_상태에서_빈_플레이리스트_목록을_올바르게_처리한다`() {
        // Given
        val emptyPlaylists = emptyList<PlaylistInfo>()
        val successState = PlaylistsScreenState.Success(emptyPlaylists)

        // Then
        assertTrue("Should be success state", successState is PlaylistsScreenState.Success)
        assertTrue("Should have empty playlist list", successState.playlists.isEmpty())
    }

    @Test
    fun `성공_상태에서_여러_플레이리스트를_올바르게_처리한다`() {
        // Given
        val playlists =
            listOf(
                testPlaylistInfo,
                testPlaylistInfo.copy(id = 2, name = "Playlist 2"),
                testPlaylistInfo.copy(id = 3, name = "Playlist 3"),
            )
        val successState = PlaylistsScreenState.Success(playlists)

        // Then
        assertTrue("Should be success state", successState is PlaylistsScreenState.Success)
        assertEquals("Should have correct playlist count", 3, successState.playlists.size)
        assertEquals("Should have correct first playlist", "Test Playlist", successState.playlists[0].name)
        assertEquals("Should have correct second playlist", "Playlist 2", successState.playlists[1].name)
        assertEquals("Should have correct third playlist", "Playlist 3", successState.playlists[2].name)
    }

    @Test
    fun `성공_상태에서_플레이리스트_순서가_유지된다`() {
        // Given
        val playlists =
            listOf(
                testPlaylistInfo.copy(id = 3, name = "Third"),
                testPlaylistInfo.copy(id = 1, name = "First"),
                testPlaylistInfo.copy(id = 2, name = "Second"),
            )
        val successState = PlaylistsScreenState.Success(playlists)

        // Then
        assertEquals("Should maintain order", "Third", successState.playlists[0].name)
        assertEquals("Should maintain order", "First", successState.playlists[1].name)
        assertEquals("Should maintain order", "Second", successState.playlists[2].name)
    }

    @Test
    fun `성공_상태에서_다양한_음악_개수를_가진_플레이리스트를_올바르게_처리한다`() {
        // Given
        val playlists =
            listOf(
                testPlaylistInfo.copy(numberOfMusic = 0),
                testPlaylistInfo.copy(id = 2, name = "Playlist 2", numberOfMusic = 10),
                testPlaylistInfo.copy(id = 3, name = "Playlist 3", numberOfMusic = 1),
            )
        val successState = PlaylistsScreenState.Success(playlists)

        // Then
        assertEquals("Should have correct first playlist count", 0, successState.playlists[0].numberOfMusic)
        assertEquals("Should have correct second playlist count", 10, successState.playlists[1].numberOfMusic)
        assertEquals("Should have correct third playlist count", 1, successState.playlists[2].numberOfMusic)
    }
}
