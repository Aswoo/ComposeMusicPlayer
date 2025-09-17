package com.sdu.composemusicplayer.domain.repository

import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.Playlist
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    val playlistsWithInfoFlows: Flow<List<PlaylistInfo>>

    fun createPlaylist(name: String)

    fun createPlaylistAndAddSongs(name: String, songUris: List<String>)

    fun addMusicToPlaylists(songsUris: List<String>, playlists: List<PlaylistInfo>)

    fun addMusicToPlaylist(musicUri: String, selectedPlayListId: Int)

    fun deletePlaylist(id: Int)

    fun renamePlaylist(id: Int, newName: String)

    fun removeMusicFromPlaylist(id: Int, songsUris: List<String>)

    suspend fun getPlaylistSongs(id: Int): List<Music>

    fun getPlaylistWithSongsFlow(playlistId: Int): Flow<Playlist>
}


