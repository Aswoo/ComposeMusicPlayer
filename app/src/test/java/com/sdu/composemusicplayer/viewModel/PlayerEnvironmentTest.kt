package com.sdu.composemusicplayer.viewModel

import android.net.Uri
import android.os.Handler
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.repository.MusicRepository
import com.sdu.composemusicplayer.mediaPlayer.service.PlayerServiceManager
import com.sdu.composemusicplayer.utils.AndroidConstants
import com.sdu.composemusicplayer.viewmodel.PlayerEnvironment
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PlayerEnvironmentTest {
    private lateinit var playerEnvironment: PlayerEnvironment
    private val mockHandler: Handler = mockk(relaxed = true)
    private val mockMusicRepository: MusicRepository = mockk(relaxed = true)
    private val mockServiceManager: PlayerServiceManager = mockk(relaxed = true)
    private val mockExoPlayer: ExoPlayer = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { mockMusicRepository.getAllMusics() } returns flowOf(emptyList())

        // Mock Uri.parse to avoid Android framework dependencies
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk<Uri>(relaxed = true)

        playerEnvironment =
            PlayerEnvironment(
                musicRepository = mockMusicRepository,
                serviceManager = mockServiceManager,
                exoPlayer = mockExoPlayer,
                handler = mockHandler,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `큐_아이템_재생시_미디어_아이템이_설정되고_재생된다`() =
        runTest {
            val musicList =
                listOf(
                    Music(
                        audioId = 1,
                        title = "Song 1",
                        artist = "Artist 1",
                        albumPath = "1",
                        duration = AndroidConstants.Time.MILLIS_IN_SECOND.toLong(),
                        audioPath = "path1",
                    ),
                    Music(
                        audioId = 2,
                        title = "Song 2",
                        artist = "Artist 2",
                        albumPath = "2",
                        duration = AndroidConstants.Time.MILLIS_IN_SECOND * 2L,
                        audioPath = "path2",
                    ),
                )
            // Removed unused queueItems variable

            playerEnvironment.updateQueue(musicList)

            playerEnvironment.playAt(AndroidConstants.Misc.DEFAULT_INDEX)

            val mediaItems = musicList.map { MediaItem.fromUri(it.audioPath) }
            verify { mockExoPlayer.setMediaItems(mediaItems, AndroidConstants.Misc.DEFAULT_INDEX, AndroidConstants.Misc.DEFAULT_DURATION) }
            verify { mockExoPlayer.prepare() }
            verify { mockExoPlayer.play() }
        }

    @Test
    fun `미디어_아이템_전환시_현재_재생_음악이_업데이트된다`() =
        runTest {
            val musicList =
                listOf(
                    Music(
                        audioId = 1,
                        title = "Song 1",
                        artist = "Artist 1",
                        albumPath = "1",
                        duration = AndroidConstants.Time.MILLIS_IN_SECOND.toLong(),
                        audioPath = "path1",
                    ),
                    Music(
                        audioId = 2,
                        title = "Song 2",
                        artist = "Artist 2",
                        albumPath = "2",
                        duration = AndroidConstants.Time.MILLIS_IN_SECOND * 2L,
                        audioPath = "path2",
                    ),
                )
            playerEnvironment.updateQueue(musicList)

            val listener = getPlayerListener()
            val mediaItem = MediaItem.fromUri(musicList[1].audioPath)

            every { mockExoPlayer.currentMediaItemIndex } returns 1

            listener.onMediaItemTransition(mediaItem, Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)

            assert(playerEnvironment.getCurrentPlayedMusic().first() == musicList[1])
            assert(playerEnvironment.getCurrentIndex().first() == 1)
        }

    private fun getPlayerListener(): Player.Listener {
        val playerListenerSlot = slot<Player.Listener>()
        verify { mockExoPlayer.addListener(capture(playerListenerSlot)) }
        return playerListenerSlot.captured
    }
}
