package com.sdu.composemusicplayer.domain.repository

import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.Playlist
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePlaylistsRepository : PlaylistsRepository {
    private val _playlistsWithInfoFlows = MutableStateFlow<List<PlaylistInfo>>(emptyList())
    override val playlistsWithInfoFlows: Flow<List<PlaylistInfo>> = _playlistsWithInfoFlows.asStateFlow()

    private val playlists = mutableListOf<PlaylistInfo>()
    private val playlistMusics = mutableMapOf<Int, List<Music>>()

    override fun createPlaylist(name: String) {
        val newId = playlists.size + 1
        playlists.add(PlaylistInfo(newId, name, 0))
        _playlistsWithInfoFlows.value = playlists.toList()
    }

    override fun createPlaylistAndAddSongs(name: String, songUris: List<String>) {
        createPlaylist(name)
        // Implementation for adding songs would go here
    }

    override fun addMusicToPlaylists(songsUris: List<String>, playlists: List<PlaylistInfo>) {
        // Implementation for adding music to multiple playlists
    }

    override fun addMusicToPlaylist(musicUri: String, selectedPlayListId: Int) {
        // Implementation for adding music to specific playlist
    }

    override fun deletePlaylist(id: Int) {
        playlists.removeAll { it.id == id }
        _playlistsWithInfoFlows.value = playlists.toList()
    }

    override fun renamePlaylist(id: Int, newName: String) {
        val index = playlists.indexOfFirst { it.id == id }
        if (index != -1) {
            val playlist = playlists[index]
            playlists[index] = playlist.copy(name = newName)
            _playlistsWithInfoFlows.value = playlists.toList()
        }
    }

    override fun removeMusicFromPlaylist(id: Int, songsUris: List<String>) {
        // Implementation for removing music from playlist
    }

    override suspend fun getPlaylistSongs(id: Int): List<Music> {
        return playlistMusics[id] ?: emptyList()
    }

    override fun getPlaylistWithSongsFlow(playlistId: Int): Flow<Playlist> {
        val playlistInfo = playlists.find { it.id == playlistId }
        val music = playlistMusics[playlistId] ?: emptyList()
        return MutableStateFlow(Playlist(playlistInfo ?: PlaylistInfo(0, "", 0), music)).asStateFlow()
    }
}
