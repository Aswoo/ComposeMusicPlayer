package com.sdu.composemusicplayer.viewModel

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sdu.composemusicplayer.domain.model.Music
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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PlayerViewModelTest {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var mockContext: Context
    private lateinit var mockEnvironment: IPlayerEnvironment

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun `셋업_테스트_환경_준비`() {
        Dispatchers.setMain(testDispatcher)

        mockContext = mockk(relaxed = true)
        mockEnvironment = mockk(relaxed = true)

        val sampleMusic =
            Music(
                audioId = 1L,
                title = "Test Song",
                artist = "Test Artist",
                duration = 180000L,
                albumPath = "/path/to/album",
                audioPath = "/path/to/test/song.mp3",
            )

        every { mockEnvironment.getAllMusics() } returns flowOf(emptyList())
        every { mockEnvironment.getCurrentPlayedMusic() } returns flowOf(sampleMusic)
        every { mockEnvironment.isPlaying() } returns flowOf(false)
        every { mockEnvironment.isBottomMusicPlayerShowed() } returns flowOf(false)
        every { mockEnvironment.getCurrentDuration() } returns flowOf(0L)
        every { mockEnvironment.isPaused() } returns flowOf(true)

        viewModel = PlayerViewModel(mockEnvironment)
    }

    @After
    fun `테스트_종료_후_환경_정리`() {
        Dispatchers.resetMain()
    }

    @Test
    fun `플레이_이벤트가_호출되면_환경의_play_함수를_실행하고_서비스를_시작한다`() =
        runTest {
            val testMusic =
                Music(
                    audioId = 1L,
                    title = "Test Music",
                    artist = "Test Artist",
                    duration = 180000L,
                    albumPath = "/path/to/album",
                    audioPath = "/path/to/test/song.mp3",
                )
            coEvery { mockEnvironment.play(testMusic, any(), any()) } returns Unit

            viewModel.onEvent(PlayerEvent.Play(testMusic))

            coVerify { mockEnvironment.play(testMusic, any(), any()) }
        }

    @Test
    fun `플레이_재생_중이_아닐_때_재생_재개를_호출한다`() =
        runTest {
            coEvery { mockEnvironment.resume() } returns Unit

            viewModel.onEvent(PlayerEvent.PlayPause(isPlaying = false))

            coVerify { mockEnvironment.resume() }
        }

    @Test
    fun `플레이_재생_중일_때_일시정지를_호출한다`() =
        runTest {
            coEvery { mockEnvironment.pause() } returns Unit

            viewModel.onEvent(PlayerEvent.PlayPause(isPlaying = true))

            coVerify { mockEnvironment.pause() }
        }

    @Test
    fun `이전_이벤트가_호출되면_환경의_previous_함수를_실행한다`() =
        runTest {
            viewModel.onEvent(PlayerEvent.Previous)

            coVerify { mockEnvironment.previous() }
        }

    @Test
    fun `다음_이벤트가_호출되면_환경의_next_함수를_실행한다`() =
        runTest {
            coEvery { mockEnvironment.next() } returns Unit

            viewModel.onEvent(PlayerEvent.Next)

            coVerify { mockEnvironment.next() }
        }

    @Test
    fun `스냅투_이벤트가_호출되면_환경의_snapTo_함수를_실행한다`() =
        runTest {
            val testDuration = 1000L
            coEvery { mockEnvironment.snapTo(testDuration) } returns Unit

            viewModel.onEvent(PlayerEvent.SnapTo(testDuration))

            coVerify { mockEnvironment.snapTo(testDuration) }
        }

    @Test
    fun `음악_리스트_업데이트_이벤트가_호출되면_환경의_updateMusicList_함수를_실행한다`() =
        runTest {
            val testMusicList =
                listOf(
                    Music(
                        audioId = 1L,
                        title = "Test Music",
                        artist = "Test Artist",
                        duration = 180000L,
                        albumPath = "/path/to/album",
                        audioPath = "/path/to/test/song.mp3",
                    ),
                    Music(
                        audioId = 2L,
                        title = "Test Music 2",
                        artist = "Test Artist 2",
                        duration = 180000L,
                        albumPath = "/path/to/album",
                        audioPath = "/path/to/test/song.mp3",
                    ),
                )
            coEvery { mockEnvironment.updateMusicList(testMusicList) } returns Unit

            viewModel.onEvent(PlayerEvent.UpdateMusicList(testMusicList))

            coVerify { mockEnvironment.updateMusicList(testMusicList) }
        }
}
