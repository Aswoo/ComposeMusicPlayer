package com.sdu.composemusicplayer.viewModel

import android.content.Context
import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import com.sdu.composemusicplayer.mediaPlayer.service.PlayerServiceManager
import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel
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
    //    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PlayerViewModel
    private lateinit var mockContext: Context
    private lateinit var mockEnvironment: IPlayerEnvironment
    private lateinit var mockServiceManager: PlayerServiceManager

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockContext = mockk(relaxed = true)
        mockEnvironment = mockk(relaxed = true)
        mockServiceManager = mockk(relaxed = true)

        val sampleMusic =
            MusicEntity(
                audioId = 1L,
                title = "Test Song",
                artist = "Test Artist",
                duration = 180000L,
                albumPath = "/path/to/album",
                audioPath = "/path/to/test/song.mp3",
            )

        // 모든 Flow 메서드에 대한 기본 mock 설정
        every { mockEnvironment.getAllMusics() } returns flowOf(emptyList())
        every { mockEnvironment.getCurrentPlayedMusic() } returns
            flowOf(
                sampleMusic,
            )
        every { mockEnvironment.isPlaying() } returns flowOf(false)
        every { mockEnvironment.isBottomMusicPlayerShowed() } returns flowOf(false)
        every { mockEnvironment.getCurrentDuration() } returns flowOf(0L)
        every { mockEnvironment.isPaused() } returns flowOf(true)

        viewModel = PlayerViewModel(mockContext, mockEnvironment, mockServiceManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent Play should call environment play and start service`() =
        runTest {
            // Arrange
            val testMusic =
                MusicEntity(
                    audioId = 1L,
                    title = "Test Music",
                    artist = "Test Artist",
                    duration = 180000L,
                    albumPath = "/path/to/album",
                    audioPath = "/path/to/test/song.mp3",
                )
            coEvery { mockEnvironment.play(testMusic) } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.Play(testMusic))

            // Assert
            coVerify { mockEnvironment.play(testMusic) }
            coVerify { mockServiceManager.startMusicService() }
        }

    @Test
    fun `onEvent PlayPause when not playing should resume and start service`() =
        runTest {
            // Arrange
            coEvery { mockEnvironment.resume() } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.PlayPause(isPlaying = false))

            // Assert
            coVerify { mockEnvironment.resume() }
            coVerify { mockServiceManager.startMusicService() }
        }

    @Test
    fun `onEvent PlayPause when playing should pause and start service`() =
        runTest {
            // Arrange
            coEvery { mockEnvironment.pause() } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.PlayPause(isPlaying = true))

            // Assert
            coVerify { mockEnvironment.pause() }
            coVerify { mockServiceManager.startMusicService() }
        }

    @Test
    fun `onEvent Previous should call environment previous`() =
        runTest {
            // Act
            viewModel.onEvent(PlayerEvent.Previous)

            // Assert
            coVerify { mockEnvironment.previous() }
        }

    @Test
    fun `onEvent Next should call environment next`() =
        runTest {
            // Arrange
            coEvery { mockEnvironment.next() } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.Next)

            // Assert
            coVerify { mockEnvironment.next() }
        }

    @Test
    fun `onEvent SnapTo should call environment snapTo`() =
        runTest {
            // Arrange
            val testDuration = 1000L
            coEvery { mockEnvironment.snapTo(testDuration) } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.SnapTo(testDuration))

            // Assert
            coVerify { mockEnvironment.snapTo(testDuration) }
        }

    @Test
    fun `onEvent UpdateMusicList should call environment updateMusicList`() =
        runTest {
            // Arrange
            val testMusicList =
                listOf(
                    MusicEntity(
                        audioId = 1L,
                        title = "Test Music",
                        artist = "Test Artist",
                        duration = 180000L,
                        albumPath = "/path/to/album",
                        audioPath = "/path/to/test/song.mp3",
                    ),
                    MusicEntity(
                        audioId = 2L,
                        title = "Test Music 2",
                        artist = "Test Artist 2",
                        duration = 180000L,
                        albumPath = "/path/to/album",
                        audioPath = "/path/to/test/song.mp3",
                    ),
                )
            coEvery { mockEnvironment.updateMusicList(testMusicList) } returns Unit

            // Act
            viewModel.onEvent(PlayerEvent.UpdateMusicList(testMusicList))

            // Assert
            coVerify { mockEnvironment.updateMusicList(testMusicList) }
        }
}
