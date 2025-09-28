package com.sdu.composemusicplayer.presentation.player

import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.SortOption
import com.sdu.composemusicplayer.domain.model.SortState
import com.sdu.composemusicplayer.utils.AndroidConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MusicUiStateTest {
    private val testMusic =
        Music(
            audioId = 1L,
            title = "Test Song",
            artist = "Test Artist",
            duration = AndroidConstants.Time.MILLIS_IN_SECOND * 180L,
            albumPath = "/test/album",
            audioPath = "/test/path",
        )

    @Test
    fun `MusicUiState의_기본값이_올바르게_설정된다`() {
        // Given
        val defaultState = MusicUiState()

        // Then
        assertTrue("Music list should be empty by default", defaultState.musicList.isEmpty())
        assertEquals("Sort state should have default values", SortState(), defaultState.sortState)
        assertEquals("Current played music should be default", Music.default, defaultState.currentPlayedMusic)
        assertFalse("Should not be playing by default", defaultState.isPlaying)
        assertFalse("Should not be paused by default", defaultState.isPaused)
        assertEquals("Current duration should be 0", 0L, defaultState.currentDuration)
        assertFalse("Should not show bottom player by default", defaultState.isBottomPlayerShow)
    }

    @Test
    fun `MusicUiState가_사용자_정의_값을_올바르게_받아들인다`() {
        // Given
        val musicList = listOf(testMusic)
        val sortState = SortState(SortOption.ARTIST)
        val customState =
            MusicUiState(
                musicList = musicList,
                sortState = sortState,
                currentPlayedMusic = testMusic,
                isPlaying = true,
                isPaused = false,
                currentDuration = 5000L,
                isBottomPlayerShow = true,
            )

        // Then
        assertEquals("Music list should match", musicList, customState.musicList)
        assertEquals("Sort state should match", sortState, customState.sortState)
        assertEquals("Current played music should match", testMusic, customState.currentPlayedMusic)
        assertTrue("Should be playing", customState.isPlaying)
        assertFalse("Should not be paused", customState.isPaused)
        assertEquals("Current duration should match", 5000L, customState.currentDuration)
        assertTrue("Should show bottom player", customState.isBottomPlayerShow)
    }

    @Test
    fun `MusicUiState가_재생과_일시정지_상태를_올바르게_처리한다`() {
        // Test playing state
        val playingState =
            MusicUiState(
                isPlaying = true,
                isPaused = false,
            )
        assertTrue("Should be playing", playingState.isPlaying)
        assertFalse("Should not be paused", playingState.isPaused)

        // Test paused state
        val pausedState =
            MusicUiState(
                isPlaying = false,
                isPaused = true,
            )
        assertFalse("Should not be playing", pausedState.isPlaying)
        assertTrue("Should be paused", pausedState.isPaused)

        // Test stopped state
        val stoppedState =
            MusicUiState(
                isPlaying = false,
                isPaused = false,
            )
        assertFalse("Should not be playing", stoppedState.isPlaying)
        assertFalse("Should not be paused", stoppedState.isPaused)
    }

    @Test
    fun `MusicUiState가_다양한_정렬_상태를_올바르게_처리한다`() {
        // Test title sort
        val titleSortState = MusicUiState(sortState = SortState(SortOption.TITLE))
        assertEquals("Should sort by title", SortOption.TITLE, titleSortState.sortState.option)

        // Test artist sort
        val artistSortState = MusicUiState(sortState = SortState(SortOption.ARTIST))
        assertEquals("Should sort by artist", SortOption.ARTIST, artistSortState.sortState.option)

        // Test default sort
        val defaultSortState = MusicUiState(sortState = SortState())
        assertEquals("Should have default sort", SortOption.TITLE, defaultSortState.sortState.option)
    }

    @Test
    fun `MusicUiState가_빈_음악_목록과_채워진_음악_목록을_올바르게_처리한다`() {
        // Test empty list
        val emptyState = MusicUiState(musicList = emptyList())
        assertTrue("Music list should be empty", emptyState.musicList.isEmpty())

        // Test populated list
        val musicList = listOf(testMusic, testMusic.copy(audioId = 2L, title = "Song 2"))
        val populatedState = MusicUiState(musicList = musicList)
        assertEquals("Music list should have correct size", 2, populatedState.musicList.size)
        assertEquals("First song should match", testMusic.title, populatedState.musicList[0].title)
        assertEquals("Second song should match", "Song 2", populatedState.musicList[1].title)
    }

    @Test
    fun `MusicUiState가_현재_재생_시간을_올바르게_처리한다`() {
        // Test zero duration
        val zeroDurationState = MusicUiState(currentDuration = 0L)
        assertEquals("Duration should be 0", 0L, zeroDurationState.currentDuration)

        // Test positive duration
        val positiveDurationState = MusicUiState(currentDuration = 30000L)
        assertEquals("Duration should match", 30000L, positiveDurationState.currentDuration)

        // Test negative duration (edge case)
        val negativeDurationState = MusicUiState(currentDuration = -1000L)
        assertEquals("Duration should match even if negative", -1000L, negativeDurationState.currentDuration)
    }
}
