package com.sdu.composemusicplayer.viewModel

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.sdu.composemusicplayer.core.database.MusicRepository
import com.sdu.composemusicplayer.core.database.QueueRepository
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.MusicQueue
import com.sdu.composemusicplayer.domain.model.QueueItem
import com.sdu.composemusicplayer.mediaPlayer.service.PlayerServiceManager
import com.sdu.composemusicplayer.viewmodel.PlayerEnvironment
import io.mockk.every
import io.mockk.mockk
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
    private val mockContext: Context = mockk(relaxed = true)
    private val mockMusicRepository: MusicRepository = mockk(relaxed = true)
    private val mockQueueRepository: QueueRepository = mockk(relaxed = true)
    private val mockServiceManager: PlayerServiceManager = mockk(relaxed = true)
    private val mockExoPlayer: ExoPlayer = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { mockMusicRepository.getAllMusics() } returns flowOf(emptyList())
        playerEnvironment =
            PlayerEnvironment(
                context = mockContext,
                musicRepository = mockMusicRepository,
                queueRepository = mockQueueRepository,
                serviceManager = mockServiceManager,
                exoPlayer = mockExoPlayer,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `playQueueItemAt should set media items and play`() =
        runTest {
            val musicList =
                listOf(
                    Music(audioId = 1, title = "Song 1", artist = "Artist 1", albumPath = "1", duration = 1000, audioPath = "path1"),
                    Music(audioId = 2, title = "Song 2", artist = "Artist 2", albumPath = "2", duration = 2000, audioPath = "path2"),
                )
            val queueItems = musicList.mapIndexed { index, music -> QueueItem(music, index) }.toMutableList()
            val musicQueue = MusicQueue(items = queueItems, currentIndex = 0)

            playerEnvironment.updateQueue(musicList)

            playerEnvironment.playAt(0)

            val mediaItems = musicList.map { MediaItem.fromUri(it.audioPath) }
            verify { mockExoPlayer.setMediaItems(mediaItems, 0, 0L) }
            verify { mockExoPlayer.prepare() }
            verify { mockExoPlayer.play() }
        }

    @Test
    fun `onMediaItemTransition should update current played music`() =
        runTest {
            val musicList =
                listOf(
                    Music(audioId = 1, title = "Song 1", artist = "Artist 1", albumPath = "1", duration = 1000, audioPath = "path1"),
                    Music(audioId = 2, title = "Song 2", artist = "Artist 2", albumPath = "2", duration = 2000, audioPath = "path2"),
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
