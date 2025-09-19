package com.sdu.composemusicplayer.domain.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FakePlaylistsRepositoryValidationTest {
    private lateinit var fakeRepository: FakePlaylistsRepository

    @Before
    fun setup() {
        fakeRepository = FakePlaylistsRepository()
    }

    @Test
    fun `fakeRepository가_올바르게_초기화된다`() {
        // Verify that the repository is properly initialized
        assertNotNull("FakePlaylistsRepository should not be null", fakeRepository)
    }

    @Test
    fun `유효한_이름으로_플레이리스트_생성시_예외가_발생하지_않는다`() = runTest {
        // Given
        val playlistName = "Test Playlist"

        // When & Then
        fakeRepository.createPlaylist(playlistName)
        // Should not throw any exception
    }

    @Test
    fun `빈_이름으로_플레이리스트_생성시_예외가_발생하지_않는다`() = runTest {
        // Given
        val emptyName = ""

        // When & Then
        fakeRepository.createPlaylist(emptyName)
        // Should not throw any exception
    }

    @Test
    fun `유효한_ID로_플레이리스트_삭제시_예외가_발생하지_않는다`() = runTest {
        // Given
        val playlistId = 1

        // When & Then
        fakeRepository.deletePlaylist(playlistId)
        // Should not throw any exception
    }

    @Test
    fun `존재하지_않는_ID로_플레이리스트_삭제시_예외가_발생하지_않는다`() = runTest {
        // Given
        val nonExistentId = 999

        // When & Then
        fakeRepository.deletePlaylist(nonExistentId)
        // Should not throw any exception
    }

    @Test
    fun `유효한_파라미터로_플레이리스트_이름_변경시_예외가_발생하지_않는다`() = runTest {
        // Given
        val playlistId = 1
        val newName = "New Name"

        // When & Then
        fakeRepository.renamePlaylist(playlistId, newName)
        // Should not throw any exception
    }

    @Test
    fun `존재하지_않는_ID로_플레이리스트_이름_변경시_예외가_발생하지_않는다`() = runTest {
        // Given
        val nonExistentId = 999
        val newName = "New Name"

        // When & Then
        fakeRepository.renamePlaylist(nonExistentId, newName)
        // Should not throw any exception
    }

    @Test
    fun `유효한_ID로_플레이리스트_가져오기시_예외가_발생하지_않는다`() = runTest {
        // Given
        val playlistId = 1

        // When & Then
        fakeRepository.getPlaylistWithSongsFlow(playlistId)
        // Should not throw any exception
    }

    @Test
    fun `존재하지_않는_ID로_플레이리스트_가져오기시_예외가_발생하지_않는다`() = runTest {
        // Given
        val nonExistentId = 999

        // When & Then
        fakeRepository.getPlaylistWithSongsFlow(nonExistentId)
        // Should not throw any exception
    }
}
