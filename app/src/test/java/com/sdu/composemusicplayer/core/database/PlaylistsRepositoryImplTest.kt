package com.sdu.composemusicplayer.core.database

import com.sdu.composemusicplayer.core.database.dao.PlaylistDao
import com.sdu.composemusicplayer.core.database.entity.PlaylistEntity
import com.sdu.composemusicplayer.core.database.model.PlaylistInfoWithNumberOfMusic
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.Playlist
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import com.sdu.composemusicplayer.domain.repository.MusicRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PlaylistsRepositoryImplTest {

    private lateinit var playlistsRepository: PlaylistsRepositoryImpl
    private lateinit var mockPlaylistDao: PlaylistDao
    private lateinit var mockMusicRepository: MusicRepository

    @Before
    fun setUp() {
        // Mock 객체들 생성
        mockPlaylistDao = mockk(relaxed = true)
        mockMusicRepository = mockk(relaxed = true)

        // Repository 인스턴스 생성
        playlistsRepository = PlaylistsRepositoryImpl(mockPlaylistDao, mockMusicRepository)
    }

    @Test
    fun `createPlaylist는_DAO의_createPlaylist를_호출한다`() = runTest {
        // Given
        val playlistName = "Test Playlist"

        // When
        playlistsRepository.createPlaylist(playlistName)

        // Then
        coVerify { mockPlaylistDao.createPlaylist(any()) }
    }

    @Test
    fun `createPlaylistAndAddSongs는_DAO의_createPlaylistAndAddSongs를_호출한다`() = runTest {
        // Given
        val playlistName = "Test Playlist"
        val songUris = listOf("uri1", "uri2", "uri3")
        coEvery { mockPlaylistDao.createPlaylistAndAddSongs(any(), any()) } returns Unit

        // When
        playlistsRepository.createPlaylistAndAddSongs(playlistName, songUris)

        // Then
        coVerify { mockPlaylistDao.createPlaylistAndAddSongs(playlistName, songUris) }
    }

    @Test
    fun `addMusicToPlaylist는_DAO의_insertSongToPlaylist를_호출한다`() = runTest {
        // Given
        val musicUri = "test_uri"
        val playlistId = 1
        coEvery { mockPlaylistDao.insertSongToPlaylist(any(), any()) } returns Unit

        // When
        playlistsRepository.addMusicToPlaylist(musicUri, playlistId)

        // Then
        coVerify { mockPlaylistDao.insertSongToPlaylist(playlistId, musicUri) }
    }

    @Test
    fun `deletePlaylist는_DAO의_deletePlaylistWithSongs를_호출한다`() = runTest {
        // Given
        val playlistId = 1
        coEvery { mockPlaylistDao.deletePlaylistWithSongs(any()) } returns Unit

        // When
        playlistsRepository.deletePlaylist(playlistId)

        // Then
        coVerify { mockPlaylistDao.deletePlaylistWithSongs(playlistId) }
    }

    @Test
    fun `renamePlaylist는_DAO의_renamePlaylist를_호출한다`() = runTest {
        // Given
        val playlistId = 1
        val newName = "New Playlist Name"
        coEvery { mockPlaylistDao.renamePlaylist(any(), any()) } returns Unit

        // When
        playlistsRepository.renamePlaylist(playlistId, newName)

        // Then
        coVerify { mockPlaylistDao.renamePlaylist(playlistId, newName) }
    }

    @Test
    fun `removeMusicFromPlaylist는_DAO의_removeSongsFromPlaylist를_호출한다`() = runTest {
        // Given
        val playlistId = 1
        val songUris = listOf("uri1", "uri2")
        coEvery { mockPlaylistDao.removeSongsFromPlaylist(any(), any()) } returns Unit

        // When
        playlistsRepository.removeMusicFromPlaylist(playlistId, songUris)

        // Then
        coVerify { mockPlaylistDao.removeSongsFromPlaylist(playlistId, songUris) }
    }

    @Test
    fun `getPlaylistSongs는_음악_리스트를_반환한다`() = runTest {
        // Given
        val playlistId = 1
        val songUris = listOf("uri1", "uri2")
        val musicEntities = listOf(
            createTestMusicEntity(1, "uri1"),
            createTestMusicEntity(2, "uri2")
        )
        
        coEvery { mockPlaylistDao.getPlaylistSongs(playlistId) } returns songUris
        every { mockMusicRepository.getAllMusics() } returns flowOf(musicEntities)

        // When
        val result = playlistsRepository.getPlaylistSongs(playlistId)

        // Then
        assertEquals(2, result.size)
        assertEquals("Test Song 1", result[0].title)
        assertEquals("Test Song 2", result[1].title)
    }

    @Test
    fun `getPlaylistSongs는_빈_리스트를_반환한다`() = runTest {
        // Given
        val playlistId = 1
        val songUris = emptyList<String>()
        val musicEntities = emptyList<com.sdu.composemusicplayer.core.database.entity.MusicEntity>()
        
        coEvery { mockPlaylistDao.getPlaylistSongs(playlistId) } returns songUris
        every { mockMusicRepository.getAllMusics() } returns flowOf(musicEntities)

        // When
        val result = playlistsRepository.getPlaylistSongs(playlistId)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `addMusicToPlaylists는_DAO의_insertSongsToPlaylists를_호출한다`() = runTest {
        // Given
        val songUris = listOf("uri1", "uri2")
        val playlists = listOf(
            PlaylistInfo(1, "Playlist 1", 0),
            PlaylistInfo(2, "Playlist 2", 0)
        )

        // When
        playlistsRepository.addMusicToPlaylists(songUris, playlists)

        // Then
        coVerify { mockPlaylistDao.insertSongsToPlaylists(songUris, any()) }
    }

    // Helper functions
    private fun createTestMusicEntity(id: Long, audioPath: String): com.sdu.composemusicplayer.core.database.entity.MusicEntity {
        return com.sdu.composemusicplayer.core.database.entity.MusicEntity(
            audioId = id,
            title = "Test Song $id",
            artist = "Test Artist $id",
            duration = 180000L,
            albumPath = "content://media/external/audio/albumart/$id",
            audioPath = audioPath
        )
    }
}
