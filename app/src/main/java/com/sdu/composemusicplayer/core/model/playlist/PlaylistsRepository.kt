package com.sdu.composemusicplayer.core.model.playlist

import com.sdu.composemusicplayer.core.database.MusicRepository
import com.sdu.composemusicplayer.core.database.dao.PlaylistDao
import com.sdu.composemusicplayer.core.database.entity.PlaylistEntity
import com.sdu.composemusicplayer.core.database.mapper.toDomain
import com.sdu.composemusicplayer.core.database.model.PlaylistInfoWithNumberOfMusic
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.Playlist
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistsRepository
    @Inject
    constructor(
        private val playlistsDao: PlaylistDao,
        private val musicRepository: MusicRepository,
    ) {
        private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

        val playlistsWithInfoFlows =
            playlistsDao
                .getPlaylistsInfoFlow()
                .map {
                    it.toDomainPlaylists()
                }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), listOf())

        fun createPlaylist(name: String) {
            coroutineScope.launch {
                val playlist = PlaylistEntity(name = name)
                playlistsDao.createPlaylist(playlist)
            }
        }

        fun createPlaylistAndAddSongs(
            name: String,
            songUris: List<String>,
        ) {
            coroutineScope.launch {
                playlistsDao.createPlaylistAndAddSongs(name, songUris)
            }
        }

        fun addMusicToPlaylists(
            songsUris: List<String>,
            playlists: List<PlaylistInfo>,
        ) {
            coroutineScope.launch {
                playlistsDao.insertSongsToPlaylists(songsUris, playlists.toDBEntities())
            }
        }

        fun addMusicToPlaylist(
            musicUri: String,
            selectedPlayListId: Int,
        ) {
            coroutineScope.launch {
                playlistsDao.insertSongToPlaylist(selectedPlayListId, musicUri)
            }
        }

        fun deletePlaylist(id: Int) {
            coroutineScope.launch {
                playlistsDao.deletePlaylistWithSongs(id)
            }
        }

        fun renamePlaylist(
            id: Int,
            newName: String,
        ) {
            coroutineScope.launch {
                playlistsDao.renamePlaylist(id, newName)
            }
        }

        fun removeMusicFromPlaylist(
            id: Int,
            songsUris: List<String>,
        ) {
            coroutineScope.launch {
                playlistsDao.removeSongsFromPlaylist(id, songsUris)
            }
        }

        suspend fun getPlaylistSongs(id: Int): List<Music> {
            val songUris = playlistsDao.getPlaylistSongs(id)
            val musicList =
                musicRepository.getAllMusics().first().map {
                    it.toDomain()
                }
            val musicMapper = MusicUriMapper(musicList = musicList)
            return musicMapper.getMusicByUris(songUris)
        }

        fun getPlaylistWithSongsFlow(playlistId: Int): Flow<Playlist> =
            combine(
                musicRepository.getAllMusics(),
                playlistsDao.getPlaylistWithSongsFlow(playlistId),
            ) { musicEntities, playlistWithSongs ->

                val musicList =
                    musicEntities.map {
                        it.toDomain()
                    }
                val musicMapper = MusicUriMapper(musicList = musicList)
                // Convert the songs to a map to enable fast retrieval
                val musicMap = musicMapper.musicList.associateBy { it.audioPath }

                // The uris of the song
                val playlistSongsUriStrings = playlistWithSongs.songUris

                val playlistSongs = mutableListOf<Music>()
                for (uriString in playlistSongsUriStrings.map { it.musicUriString }) {
                    val music = musicMap[uriString]
                    if (music != null) {
                        playlistSongs.add(music)
                    }
                }

                val playlistInfo = playlistWithSongs.playlistEntity
                Playlist(
                    PlaylistInfo(playlistInfo.id, playlistInfo.name, playlistSongs.size),
                    playlistSongs,
                )
            }

        private fun PlaylistInfo.toDBEntity() = PlaylistEntity(id, name)

        private fun List<PlaylistInfo>.toDBEntities() = map { it.toDBEntity() }

        private fun PlaylistInfoWithNumberOfMusic.toDomainPlaylist() =
            PlaylistInfo(
                id = playlistEntity.id,
                name = playlistEntity.name,
                numberOfMusic = numberOfMusic,
            )

        private fun List<PlaylistInfoWithNumberOfMusic>.toDomainPlaylists() = map { it.toDomainPlaylist() }
    }
