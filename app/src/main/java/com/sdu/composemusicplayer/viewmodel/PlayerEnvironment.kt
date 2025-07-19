package com.sdu.composemusicplayer.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.sdu.composemusicplayer.core.database.MusicRepository
import com.sdu.composemusicplayer.core.database.QueueRepository
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import com.sdu.composemusicplayer.core.database.mapper.toDomain
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.MusicQueue
import com.sdu.composemusicplayer.domain.model.PlaySource
import com.sdu.composemusicplayer.domain.model.QueueItem
import com.sdu.composemusicplayer.mediaPlayer.service.PlayerServiceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerEnvironment
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val musicRepository: MusicRepository,
        private val queueRepository: QueueRepository,
        private val serviceManager: PlayerServiceManager,
        private val exoPlayer: ExoPlayer,
    ) : IPlayerEnvironment {
        private val dispatcher = Dispatchers.IO
        private val scope = CoroutineScope(dispatcher + SupervisorJob())

        // 전체 음악 목록 (변경 없음)
        private val _allMusics = MutableStateFlow<List<Music>>(emptyList())

        override fun getAllMusics(): Flow<List<Music>> = _allMusics.asStateFlow()

        // MusicQueue 상태 관리 (큐 + 현재 인덱스)
        private val _musicQueue = MutableStateFlow(MusicQueue.EMPTY)

        override fun observeQueue(): Flow<List<Music>> =
            _musicQueue.map { queue ->
                queue.items.map { it.music }
            }

        override fun getCurrentIndex(): Flow<Int> = _musicQueue.map { it.currentIndex }

        // 현재 재생 중인 Music
        private val _currentPlayedMusic = MutableStateFlow(Music.default)

        override fun getCurrentPlayedMusic(): Flow<Music> = _currentPlayedMusic.asStateFlow()

        // 재생 상태
        private val _isPlaying = MutableStateFlow(false)

        override fun isPlaying(): Flow<Boolean> = _isPlaying.asStateFlow()

        private val _isPaused = MutableStateFlow(false)

        override fun isPaused(): Flow<Boolean> = _isPaused.asStateFlow()

        // 재생 시간
        private val _currentDuration = MutableStateFlow(0L)

        override fun getCurrentDuration(): Flow<Long> = _currentDuration.asStateFlow()

        // 하단 플레이어 노출 여부
        private val _isBottomMusicPlayerShowed = MutableStateFlow(false)

        override fun isBottomMusicPlayerShowed(): Flow<Boolean> = _isBottomMusicPlayerShowed.asStateFlow()

        private val handler = Handler(Looper.getMainLooper())
        private val updateRunnable =
            object : Runnable {
                override fun run() {
                    val position = exoPlayer.currentPosition.takeIf { exoPlayer.duration != -1L } ?: 0L
                    _currentDuration.value = position
                    handler.postDelayed(this, 1000)
                }
            }

        private val playerListener =
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        scope.launch { next() }
                    } else if (playbackState == Player.STATE_READY) {
                        serviceManager.startMusicService() // 준비 완료 시 서비스 시작
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    _isPaused.value = !isPlaying
                }
            }

        init {
            exoPlayer.addListener(playerListener)

            scope.launch {
                musicRepository
                    .getAllMusics()
                    .distinctUntilChanged()
                    .map { list -> list.map(MusicEntity::toDomain) }
                    .collect { list ->
                        _allMusics.value = list
                        if (_musicQueue.value.items.isEmpty()) {
                            _musicQueue.value =
                                MusicQueue(
                                    items = list.mapIndexed { index, music -> QueueItem(music, index) }.toMutableList(),
                                    currentIndex = 0,
                                )
                        }
                    }
            }
        }

        override suspend fun updateQueue(queue: List<Music>) {
            val newItems = queue.mapIndexed { index, music -> QueueItem(music, index) }.toMutableList()
            _musicQueue.value = MusicQueue(newItems, 0)
//        queueRepository.saveQueueFromDBQueueItems(newItems)
        }

        override suspend fun updateMusicList(musicList: List<Music>) {
            _allMusics.value = musicList
        }

        override suspend fun refreshMusicList() {
            musicRepository.syncMusicWithDevice()
        }

        override suspend fun setShowBottomMusicPlayer(isShowed: Boolean) {
            _isBottomMusicPlayerShowed.value = isShowed
        }

        override suspend fun resetIsPaused() {
            _isPaused.value = false
        }

        private fun playQueueItemAt(index: Int) {
            val queue = _musicQueue.value
            if (index !in queue.items.indices) return

            val item = queue.items[index]
            _musicQueue.value = queue.copy(currentIndex = index)
            _currentPlayedMusic.value = item.music

            exoPlayer.setMediaItem(MediaItem.fromUri(item.music.audioPath.toUri()))
            exoPlayer.prepare()
            exoPlayer.play()

            startDurationUpdates()
        }

        override suspend fun play(
            music: Music,
            playSource: PlaySource,
            playList: List<Music>?,
        ) {
            when (playSource) {
                PlaySource.PLAYLIST -> {
                    val queue = _musicQueue.value
                    val currentQueueMusicList = queue.items.map { it.music }

                    if (playList != null && currentQueueMusicList == playList) {
                        // 같은 Queue라면 현재 큐에서 index 찾아서 재생
                        val index = queue.items.indexOfFirst { it.music.audioId == music.audioId }
                        if (index != -1) {
                            playQueueItemAt(index)
                        }
                    } else if (playList != null) {
                        // 큐를 새로 설정하고 재생
                        val queueItems = playList.mapIndexed { i, m -> QueueItem(m, i) }.toMutableList()
                        val index = playList.indexOfFirst { it.audioId == music.audioId }
                        if (index != -1) {
                            _musicQueue.value = MusicQueue(queueItems, index)
                            playQueueItemAt(index)
                        }
                    }
                }
                PlaySource.SINGLE -> {
                    val musicList = _allMusics.value
                    val index = musicList.indexOfFirst { it.audioId == music.audioId }
                    if (index == -1) return

                    val queueItems = musicList.mapIndexed { i, m -> QueueItem(m, i) }.toMutableList()
                    _musicQueue.value = MusicQueue(queueItems, index)
                    playQueueItemAt(index)
                }
            }
        }

        override suspend fun playAt(index: Int) {
            playQueueItemAt(index)
        }

        override suspend fun pause() {
            exoPlayer.pause()
        }

        override suspend fun resume() {
            exoPlayer.play()
        }

        override suspend fun previous() {
            val queue = _musicQueue.value
            if (queue.skipToPrevious()) {
                playQueueItemAt(queue.currentIndex)
            } else {
                // 만약 이전곡 없으면 반복 등 추가 처리 가능
                // 여기서는 첫 곡 유지
            }
        }

        override suspend fun next() {
            val queue = _musicQueue.value
            if (queue.skipToNext()) {
                playQueueItemAt(queue.currentIndex)
            } else {
                // 마지막 곡이면 첫 곡으로 이동 (반복 재생)
                playQueueItemAt(0)
            }
        }

        override fun snapTo(
            duration: Long,
            fromUser: Boolean,
        ) {
            exoPlayer.seekTo(duration)
        }

        private fun startDurationUpdates() {
            handler.removeCallbacks(updateRunnable)
            handler.post(updateRunnable)
        }

        private fun stopDurationUpdates() {
            handler.removeCallbacks(updateRunnable)
            _currentDuration.value = 0L
        }
    }
