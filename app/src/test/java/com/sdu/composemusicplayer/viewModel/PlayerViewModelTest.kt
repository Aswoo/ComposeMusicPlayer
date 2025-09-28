package com.sdu.composemusicplayer.viewModel

import android.content.Context
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.PlaySource
import com.sdu.composemusicplayer.presentation.player.PlayerViewModel
import com.sdu.composemusicplayer.utils.AndroidConstants
import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PlayerViewModelTest {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var mockContext: Context
    private lateinit var mockEnvironment: IPlayerEnvironment

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockContext = mockk(relaxed = true)
        mockEnvironment = mockk(relaxed = true)

        val sampleMusic = createTestMusic(1)

        // 모든 Flow 메서드에 대한 기본 mock 설정
        every { mockEnvironment.getAllMusics() } returns flowOf(emptyList())
        every { mockEnvironment.getCurrentPlayedMusic() } returns flowOf(sampleMusic)
        every { mockEnvironment.isPlaying() } returns flowOf(false)
        every { mockEnvironment.isBottomMusicPlayerShowed() } returns flowOf(false)
        every { mockEnvironment.getCurrentDuration() } returns flowOf(AndroidConstants.Misc.DEFAULT_DURATION)
        every { mockEnvironment.isPaused() } returns flowOf(true)

        viewModel = PlayerViewModel(mockEnvironment)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Play_이벤트시_환경의_play가_호출된다`() =
        runTest {
            // Arrange
            val testMusic = createTestMusic(1)
            coEvery { mockEnvironment.play(testMusic, PlaySource.SINGLE) } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.Play(testMusic))

            // Assert
            coVerify { mockEnvironment.play(testMusic, PlaySource.SINGLE) }
        }

    @Test
    fun `재생중이_아닐때_PlayPause_이벤트시_재생이_재개된다`() =
        runTest {
            // Arrange
            coEvery { mockEnvironment.resume() } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.PlayPause(isPlaying = false))

            // Assert
            coVerify { mockEnvironment.resume() }
        }

    @Test
    fun `재생중일때_PlayPause_이벤트시_일시정지된다`() =
        runTest {
            // Arrange
            coEvery { mockEnvironment.pause() } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.PlayPause(isPlaying = true))

            // Assert
            coVerify { mockEnvironment.pause() }
        }

    @Test
    fun `Previous_이벤트시_환경의_previous가_호출된다`() =
        runTest {
            // Act
            viewModel.onEvent(PlayerEvent.Previous)

            // Assert
            coVerify { mockEnvironment.previous() }
        }

    @Test
    fun `Next_이벤트시_환경의_next가_호출된다`() =
        runTest {
            // Arrange
            coEvery { mockEnvironment.next() } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.Next)

            // Assert
            coVerify { mockEnvironment.next() }
        }

    @Test
    fun `SnapTo_이벤트시_환경의_snapTo가_호출된다`() =
        runTest {
            // Arrange
            val testDuration = AndroidConstants.Time.MILLIS_IN_SECOND.toLong()
            coEvery { mockEnvironment.snapTo(testDuration) } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.SnapTo(testDuration))

            // Assert
            coVerify { mockEnvironment.snapTo(testDuration) }
        }

    @Test
    fun `UpdateMusicList_이벤트시_환경의_updateMusicList가_호출된다`() =
        runTest {
            // Arrange
            val testMusicList = listOf(createTestMusic(1), createTestMusic(2))
            coEvery { mockEnvironment.updateMusicList(testMusicList) } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.UpdateMusicList(testMusicList))

            // Assert
            coVerify { mockEnvironment.updateMusicList(testMusicList) }
        }

    private fun createTestMusic(id: Long): Music {
        return Music(
            audioId = id,
            title = "Test Music $id",
            artist = "Test Artist $id",
            duration = AndroidConstants.Time.SECONDS_IN_MINUTE * 3L * AndroidConstants.Time.MILLIS_IN_SECOND, // 3분
            albumPath = "/path/to/album",
            audioPath = "/path/to/test/song$id.mp3",
        )
    }
}
