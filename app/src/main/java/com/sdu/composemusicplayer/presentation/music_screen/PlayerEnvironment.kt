package com.sdu.composemusicplayer.presentation.music_screen

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import com.sdu.composemusicplayer.data.roomdb.MusicRepository
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

class PlayerEnvironment @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository
) {

    val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _allMusics = MutableStateFlow(emptyList<MusicEntity>())
    private val allMusics: StateFlow<List<MusicEntity>> = _allMusics.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPlayedMusic = MutableStateFlow(MusicEntity.default)
    val currentPlayedMusic: StateFlow<MusicEntity> = _currentPlayedMusic

    private val _playBackMode = MutableStateFlow(PlayBackMode.REPEAT_ONE)
    val playbackMode: StateFlow<PlayBackMode> = _playBackMode

    private val _hasStopped = MutableStateFlow(false)
    val hasStopped: StateFlow<Boolean> = _hasStopped

    private val _isBottomMusicPlayerShowed = MutableStateFlow(false)
    val isBottomMusicPlayerShowed: StateFlow<Boolean> = _isBottomMusicPlayerShowed

    private val playerhandler: Handler = Handler((Looper.getMainLooper()))

    private val exoPlayer = ExoPlayer.Builder(context).build().apply {
        addListener(object : Player.Listener {
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
                            val currentIndex = allMusics.value.indexOfFirst {
                                it.audioId == currentPlayedMusic.value.audioId
                            }
                            val nextSong = when {
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
        })
    }

    init {
        CoroutineScope(dispatcher).launch {
            musicRepository.getAllMusics().distinctUntilChanged().collect {
                _allMusics.emit(it)
            }
        }
    }

    fun getAllMusics(): Flow<List<MusicEntity>> {
        return allMusics
    }

    fun isBottomMusicPlayerShowed(): Flow<Boolean> {
        return isBottomMusicPlayerShowed
    }

    suspend fun play(music: MusicEntity) {
        if (music.audioId != MusicEntity.default.audioId) {
            _hasStopped.emit(false)
            _currentPlayedMusic.emit(music)

            playerhandler.post {
                exoPlayer.setMediaItem(MediaItem.fromUri(music.audioPath.toUri()))
                exoPlayer.prepare()
                exoPlayer.play()
            }
        }
    }

    suspend fun pause() {
        playerhandler.post { exoPlayer.pause() }
    }

    suspend fun resume() {
        if (hasStopped.value && currentPlayedMusic.value != MusicEntity.default) {
            play(currentPlayedMusic.value)
        } else playerhandler.post { exoPlayer.play() }
    }

    suspend fun setShowBottomMusicPlayer(isShowed: Boolean) {
        _isBottomMusicPlayerShowed.emit(isShowed)
    }

    suspend fun refreshMusicList() {
        val scannedMusics = MusicUtil.fetchMusicFromDevice(context = context)
    }
}

enum class PlayBackMode {
    REPEAT_ONE,
    REPEAT_ALL,
    REPEAT_OFF
}