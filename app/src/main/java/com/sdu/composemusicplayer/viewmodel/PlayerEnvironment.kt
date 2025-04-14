package com.sdu.composemusicplayer.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.sdu.composemusicplayer.data.music.MusicEntity
import com.sdu.composemusicplayer.data.music.MusicRepository
import com.sdu.composemusicplayer.utils.MusicUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerEnvironment
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val musicRepository: MusicRepository,
        private val exoPlayer: ExoPlayer,
    ) : IPlayerEnvironment {
        val dispatcher: CoroutineDispatcher = Dispatchers.IO

        private val _allMusics = MutableStateFlow(emptyList<MusicEntity>())
        private val allMusics: StateFlow<List<MusicEntity>> = _allMusics.asStateFlow()

        private val _currentDuration = MutableStateFlow(0L)
        private val currentDuration: StateFlow<Long> = _currentDuration

        private val _isPlaying = MutableStateFlow(false)
        val isPlaying: StateFlow<Boolean> = _isPlaying

        private val _currentPlayedMusic = MutableStateFlow(MusicEntity.default)
        val currentPlayedMusic: StateFlow<MusicEntity> = _currentPlayedMusic

        private val _playbackMode = MutableStateFlow(PlayBackMode.REPEAT_ONE)
        val playbackMode: StateFlow<PlayBackMode> = _playbackMode

        private val _hasStopped = MutableStateFlow(false)
        val hasStopped: StateFlow<Boolean> = _hasStopped

        private val _isBottomMusicPlayerShowed = MutableStateFlow(false)
        val isBottomMusicPlayerShowed: StateFlow<Boolean> = _isBottomMusicPlayerShowed

        private val _isPaused = MutableStateFlow(false)
        private val isPaused: StateFlow<Boolean> = _isPaused

        private val playerHandler: Handler = Handler((Looper.getMainLooper()))

        private var playingRunnable: Runnable =
            kotlinx.coroutines.Runnable {
            }
        private val playingHandler: Handler = Handler((Looper.getMainLooper()))

        init {
            CoroutineScope(dispatcher).launch {
                musicRepository.getAllMusics().distinctUntilChanged().collect {
                    _allMusics.emit(it)
                }
            }
            exoPlayer.apply {
                addListener(
                    object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            if (playbackState == ExoPlayer.STATE_ENDED) {
                                when (playbackMode.value) {
                                    PlayBackMode.REPEAT_ONE -> {
                                        CoroutineScope(dispatcher).launch {
                                            play(currentPlayedMusic.value)
                                        }
                                    }

                                    PlayBackMode.REPEAT_ALL -> {
                                        val currentIndex =
                                            allMusics.value.indexOfFirst {
                                                it.audioId == currentPlayedMusic.value.audioId
                                            }
                                        val nextSong =
                                            when {
                                                currentIndex == allMusics.value.lastIndex -> allMusics.value[0]
                                                currentIndex != -1 -> allMusics.value[currentIndex + 1]
                                                else -> allMusics.value[0]
                                            }
                                        CoroutineScope(dispatcher).launch {
                                            play(nextSong)
                                        }
                                    }

                                    PlayBackMode.REPEAT_OFF -> {
                                        this@apply.stop()
                                        _currentPlayedMusic.tryEmit(MusicEntity.default)
                                    }
                                }
                            }
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            _isPlaying.tryEmit(isPlaying)
                        }
                    },
                )
            }
        }

        override fun getAllMusics(): Flow<List<MusicEntity>> {
            return allMusics
        }

        override fun getCurrentPlayedMusic(): Flow<MusicEntity> {
            return currentPlayedMusic
        }

        override fun isPlaying(): Flow<Boolean> = isPlaying

        override fun isBottomMusicPlayerShowed(): Flow<Boolean> {
            return isBottomMusicPlayerShowed
        }

        override fun getCurrentDuration(): Flow<Long> = currentDuration

        override fun isPaused(): Flow<Boolean> = isPaused

        override suspend fun resetIsPaused() {
            _isPaused.emit(false)
        }

        override suspend fun play(music: MusicEntity) {
            if (music.audioId != MusicEntity.default.audioId) {
                _hasStopped.emit(false)
                _currentPlayedMusic.emit(music)

                playerHandler.post {
                    exoPlayer.setMediaItem(MediaItem.fromUri(music.audioPath.toUri()))
                    exoPlayer.prepare()
                    exoPlayer.play()
                }

                playingRunnable =
                    kotlinx.coroutines.Runnable {
                        val duration = if (exoPlayer.duration != -1L) exoPlayer.currentPosition else 0L
                        _currentDuration.tryEmit(duration)

                        playingHandler.postDelayed(playingRunnable, 1000)
                    }
                playingHandler.post(playingRunnable)
            }
        }

        override suspend fun pause() {
            playerHandler.post { exoPlayer.pause() }
            _isPaused.emit(true)
        }

        override suspend fun resume() {
            if (hasStopped.value && currentPlayedMusic.value != MusicEntity.default) {
                play(currentPlayedMusic.value)
            } else {
                playerHandler.post { exoPlayer.play() }
            }
        }

        override suspend fun previous() {
            val currentIndex =
                allMusics.value.indexOfFirst { it.audioId == currentPlayedMusic.value.audioId }

            val previousMusic =
                when {
                    currentIndex == 0 -> allMusics.value[allMusics.value.lastIndex]
                    currentIndex >= 1 -> allMusics.value[currentIndex - 1]
                    else -> allMusics.value[0]
                }

            CoroutineScope(dispatcher).launch {
                play(previousMusic)
            }
        }

        override suspend fun next() {
            val currentIndex =
                allMusics.value.indexOfFirst {
                    it.audioId == currentPlayedMusic.value.audioId
                }
            val nextMuisc =
                when {
                    currentIndex == allMusics.value.lastIndex -> allMusics.value[0]
                    currentIndex != -1 -> allMusics.value[currentIndex + 1]
                    else -> allMusics.value[0]
                }
            CoroutineScope(dispatcher).launch {
                play(nextMuisc)
            }
        }

        override fun snapTo(
            duration: Long,
            fromUser: Boolean,
        ) {
            _currentDuration.tryEmit(duration)
            if (fromUser) playerHandler.post { exoPlayer.seekTo(duration) }
        }

        override suspend fun setShowBottomMusicPlayer(isShowed: Boolean) {
            _isBottomMusicPlayerShowed.emit(isShowed)
        }

        override suspend fun updateMusicList(musicList: List<MusicEntity>) {
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
                if (it.audioId !in newMusicsIDs) musicsToDelete.add(it)
            }

            musicRepository.insertMusics(*musicsToInsert.toTypedArray())
            musicRepository.deleteMusics(*musicsToDelete.toTypedArray())
        }
    }

enum class PlayBackMode {
    REPEAT_ONE,
    REPEAT_ALL,
    REPEAT_OFF,
}
