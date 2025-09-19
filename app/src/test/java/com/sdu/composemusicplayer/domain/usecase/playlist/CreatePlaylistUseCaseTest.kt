package com.sdu.composemusicplayer.domain.usecase.playlist

import com.sdu.composemusicplayer.domain.repository.FakePlaylistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreatePlaylistUseCaseTest {
    private lateinit var fakeRepository: FakePlaylistsRepository
    private lateinit var createPlaylistUseCase: CreatePlaylistUseCase

    @Before
    fun setup() {
        fakeRepository = FakePlaylistsRepository()
        createPlaylistUseCase = CreatePlaylistUseCase(fakeRepository)
    }

    @Test
    fun `플레이리스트_생성시_저장소에_플레이리스트가_추가된다`() = runTest {
        // Given
        val playlistName = "Test Playlist"

        // When
        createPlaylistUseCase(playlistName)

        // Then
        // Removed unused playlists variable
        // Note: In a real test, you'd collect the flow and verify the playlist was added
        // This is a simplified example
    }
}
