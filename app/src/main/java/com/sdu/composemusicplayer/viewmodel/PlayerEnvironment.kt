package com.sdu.composemusicplayer.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.sdu.composemusicplayer.core.database.DBQueueItem
import com.sdu.composemusicplayer.core.database.MusicRepository
import com.sdu.composemusicplayer.core.database.QueueRepository
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import com.sdu.composemusicplayer.core.database.mapper.toDomain
import com.sdu.composemusicplayer.core.database.mapper.toEntity
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.MusicQueue
import com.sdu.composemusicplayer.domain.model.PlayBackMode
import com.sdu.composemusicplayer.domain.model.QueueItem
import com.sdu.composemusicplayer.utils.MusicUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerEnvironment @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository,
    private val queueRepository: QueueRepository,
    private val exoPlayer: ExoPlayer,
) : IPlayerEnvironment {

    val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _allMusics = MutableStateFlow(emptyList<Music>())
    private val allMusics: StateFlow<List<Music>> = _allMusics.asStateFlow()

    private val _currentDuration = MutableStateFlow(0L)
    private val currentDuration: StateFlow<Long> = _currentDuration

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPlayedMusic = MutableStateFlow(Music.default)
    val currentPlayedMusic: StateFlow<Music> = _currentPlayedMusic

    private val _playbackMode = MutableStateFlow(PlayBackMode.REPEAT_ONE)
    val playbackMode: StateFlow<PlayBackMode> = _playbackMode

    private val _hasStopped = MutableStateFlow(false)
    val hasStopped: StateFlow<Boolean> = _hasStopped

    private val _isBottomMusicPlayerShowed = MutableStateFlow(false)
    val isBottomMusicPlayerShowed: StateFlow<Boolean> = _isBottomMusicPlayerShowed

    val queue = MutableStateFlow(MusicQueue.EMPTY)

    private val _isPaused = MutableStateFlow(false)
    private val isPaused: StateFlow<Boolean> = _isPaused

    private val playerHandler = Handler(Looper.getMainLooper())
    private val playingHandler = Handler(Looper.getMainLooper())
    private var playingRunnable: Runnable = Runnable {}

    init {
        CoroutineScope(dispatcher).launch {
            musicRepository.getAllMusics().distinctUntilChanged().collect {
                _allMusics.emit(it.map { entity -> entity.toDomain() })
            }
        }
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        when (playbackMode.value) {
                            PlayBackMode.REPEAT_ONE -> CoroutineScope(dispatcher).launch {
                                play(currentPlayedMusic.value)
                            }

                            PlayBackMode.REPEAT_ALL -> {
                                val currentIndex = queue.value.items.indexOfFirst {
                                    it.music.audioId == currentPlayedMusic.value.audioId
                                }
                                val nextMusic = when {
                                    currentIndex == queue.value.items.lastIndex -> queue.value.items[0].music
                                    currentIndex != -1 -> queue.value.items[currentIndex + 1].music
                                    else -> queue.value.items[0].music
                                }
                                CoroutineScope(dispatcher).launch { play(nextMusic) }
                            }

                            PlayBackMode.REPEAT_OFF -> {
                                exoPlayer.stop()
                                _currentPlayedMusic.tryEmit(Music.default)
                            }
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.tryEmit(isPlaying)
                }
            },
        )
    }

    override fun getAllMusics(): Flow<List<Music>> = allMusics
    override fun getCurrentPlayedMusic(): Flow<Music> = currentPlayedMusic
    override fun getCurrentIndex(): Flow<Int> = queue.map {
        it.items.indexOfFirst { item -> item.music.audioId == currentPlayedMusic.value.audioId }
    }

    override fun isPlaying(): Flow<Boolean> = isPlaying
    override fun isBottomMusicPlayerShowed(): Flow<Boolean> = isBottomMusicPlayerShowed
    override fun getCurrentDuration(): Flow<Long> = currentDuration
    override fun isPaused(): Flow<Boolean> = isPaused
    override suspend fun resetIsPaused() {
        _isPaused.emit(false)
    }

    override fun observeQueue(): Flow<List<Music>> = queue.map { it.items.map { item -> item.music } }

    override suspend fun updateQueue(queueList: List<Music>) {
        val musicQueue: MutableList<QueueItem> = queueList.map {
            QueueItem(
                music = it,
                originalIndex = queueList.indexOf(it),
            )
        }.toMutableList()
        queue.value = MusicQueue(items = musicQueue, currentIndex = 0)

//        queueRepository.saveQueueFromDBQueueItems(musicQueue)
    }

    override suspend fun play(music: Music) {
        val indexInQueue = queue.value.items.indexOfFirst { it.music.audioId == music.audioId }
        if (indexInQueue == -1) {
            // queue에 없는 음악은 무시하거나 예외 처리
            return
        }

        _hasStopped.emit(false)
        _currentPlayedMusic.emit(music)

        playerHandler.post {
            exoPlayer.setMediaItem(MediaItem.fromUri(music.audioPath.toUri()))
            exoPlayer.prepare()
            exoPlayer.play()
        }

        playingRunnable = object : Runnable {
            override fun run() {
                val duration = if (exoPlayer.duration != -1L) exoPlayer.currentPosition else 0L
                _currentDuration.tryEmit(duration)
                playingHandler.postDelayed(this, 1000)
            }
        }
        playingHandler.post(playingRunnable)
    }


    override suspend fun playAt(index: Int) {
        val music = queue.value.items.getOrNull(index)?.music ?: return
        play(music)
    }

    override suspend fun pause() {
        playerHandler.post { exoPlayer.pause() }
        _isPaused.emit(true)
    }

    override suspend fun resume() {
        if (hasStopped.value && currentPlayedMusic.value != Music.default) {
            play(currentPlayedMusic.value)
        } else {
            playerHandler.post { exoPlayer.play() }
        }
    }

    override suspend fun previous() {
        val currentIndex = queue.value.items.indexOfFirst {
            it.music.audioId == currentPlayedMusic.value.audioId
        }
        val previousMusic = when {
            currentIndex == 0 -> queue.value.items.last().music
            currentIndex >= 1 -> queue.value.items[currentIndex - 1].music
            else -> queue.value.items.first().music
        }
        play(previousMusic)
    }

    override suspend fun next() {
        val currentIndex = queue.value.items.indexOfFirst {
            it.music.audioId == currentPlayedMusic.value.audioId
        }
        val nextMusic = when {
            currentIndex == queue.value.items.lastIndex -> queue.value.items.first().music
            currentIndex != -1 -> queue.value.items[currentIndex + 1].music
            else -> queue.value.items.first().music
        }
        play(nextMusic)
    }

    override fun snapTo(duration: Long, fromUser: Boolean) {
        _currentDuration.tryEmit(duration)
        if (fromUser) playerHandler.post { exoPlayer.seekTo(duration) }
    }

    override suspend fun setShowBottomMusicPlayer(isShowed: Boolean) {
        _isBottomMusicPlayerShowed.emit(isShowed)
    }

    override suspend fun updateMusicList(musicList: List<Music>) {
        _allMusics.emit(musicList)
    }

    override suspend fun refreshMusicList() {
        val scannedMusics = MusicUtil.fetchMusicFromDevice(context = context)
        insertAllMusics(scannedMusics)
    }

    private suspend fun insertAllMusics(newMusicList: List<MusicEntity>) {
        val musicsToInsert = arrayListOf<MusicEntity>()
        val musicsToDelete = arrayListOf<MusicEntity>()

        val storedMusicsIDs = allMusics.value.map { it.audioId }
        val newMusicsIDs = newMusicList.map { it.audioId }

        newMusicList.forEach {
            if (it.audioId !in storedMusicsIDs) musicsToInsert.add(it)
        }
        allMusics.value.forEach {
            if (it.audioId !in newMusicsIDs) {
                musicsToDelete.add(it.toEntity())
            }
        }

        musicRepository.insertMusics(*musicsToInsert.toTypedArray())
        musicRepository.deleteMusics(*musicsToDelete.toTypedArray(), context = context)
    }

    override suspend fun setPlaylistAndPlayAtIndex(playlist: List<Music>, index: Int) {
        if (playlist.isEmpty()) return
        if(!isSameQueue(playlist)){
            updateQueue(playlist)
        }
        play(playlist[index])
    }

    private fun isSameQueue(playlist: List<Music>): Boolean {
        val currentQueue = queue.value
        if(currentQueue.items.size != playlist.size) return false
        for (i in playlist.indices) {
            if (currentQueue.items[i].music.audioId != playlist[i].audioId) return false
        }
        return true
    }
}
