package com.sdu.composemusicplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sdu.composemusicplayer.core.database.MUSIC_URI_STRING_COLUMN
import com.sdu.composemusicplayer.core.database.PLAYLIST_ENTITY
import com.sdu.composemusicplayer.core.database.PLAYLIST_ID_COLUMN
import com.sdu.composemusicplayer.core.database.PLAYLIST_MUSIC_ENTITY
import com.sdu.composemusicplayer.core.database.PLAYLIST_NAME_COLUMN
import com.sdu.composemusicplayer.core.database.entity.PlaylistEntity
import com.sdu.composemusicplayer.core.database.entity.PlaylistsMusicEntity
import com.sdu.composemusicplayer.core.database.model.PlaylistInfoWithNumberOfMusic
import com.sdu.composemusicplayer.core.model.playlist.PlaylistWithMusicUri
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaylistDao {

    @Query(
        "SELECT P.*, S.music_uri FROM $PLAYLIST_ENTITY P LEFT OUTER JOIN $PLAYLIST_MUSIC_ENTITY S " +
                "ON P.${PLAYLIST_ID_COLUMN} = S.${PLAYLIST_ID_COLUMN} WHERE P.$PLAYLIST_ID_COLUMN = :playlistId"
    )
    fun getPlaylistWithSongsFlow(playlistId: Int): Flow<PlaylistWithMusicUri>

    @Query(
        "SELECT S.music_uri FROM $PLAYLIST_MUSIC_ENTITY S WHERE $PLAYLIST_ID_COLUMN = :playlistId"
    )
    suspend fun getPlaylistSongs(playlistId: Int): List<String>

    @Insert
    suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long

    @Transaction
    suspend fun deletePlaylistWithSongs(playlistId: Int) {
        deletePlaylistEntity(playlistId)
        deletePlaylistSongs(playlistId)
    }

    @Transaction
    suspend fun createPlaylistAndAddSongs(name: String, songsUris: List<String>) {
        val newId = createPlaylist(PlaylistEntity(name = name)).toInt()
        insertSongsToPlaylist(songsUris.map {
            PlaylistsMusicEntity(
                newId,
                it
            )
        })
    }

    @Query(
        "DELETE FROM $PLAYLIST_ENTITY WHERE $PLAYLIST_ID_COLUMN = :id"
    )
    suspend fun deletePlaylistEntity(id: Int)

    @Query(
        "DELETE FROM $PLAYLIST_MUSIC_ENTITY WHERE $PLAYLIST_ID_COLUMN = :playlistId"
    )
    suspend fun deletePlaylistSongs(playlistId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongsToPlaylist(playlistsSongsEntity: List<PlaylistsMusicEntity>)

    @Transaction
    suspend fun insertSongsToPlaylists(
        songUris: List<String>,
        playlistsMusicEntity: List<PlaylistEntity>
    ) {
        for (playlist in playlistsMusicEntity) {
            val songsEntities = songUris.map {
                PlaylistsMusicEntity(
                    playlist.id,
                    it
                )
            }
            insertSongsToPlaylist(songsEntities)
        }
    }

    @Transaction
    suspend fun insertSongToPlaylist(playlistId: Int, musicUri: String) {
        val entity = PlaylistsMusicEntity(
            playlistId = playlistId,
            musicUriString = musicUri
        )
        insertSongsToPlaylist(listOf(entity))
    }

    @Query(
        "UPDATE $PLAYLIST_ENTITY SET $PLAYLIST_NAME_COLUMN = :newName " +
                "WHERE $PLAYLIST_ID_COLUMN = :playlistId"
    )
    suspend fun renamePlaylist(playlistId: Int, newName: String)

    @Query(
        "DELETE FROM $PLAYLIST_MUSIC_ENTITY WHERE $PLAYLIST_ID_COLUMN = :playlistId AND $MUSIC_URI_STRING_COLUMN IN (:songUris)"
    )
    suspend fun removeSongsFromPlaylist(playlistId: Int, songUris: List<String>)

    @Query(
        "SELECT P.*, COUNT(S.$MUSIC_URI_STRING_COLUMN) as 'numberOfMusic' FROM $PLAYLIST_ENTITY P LEFT OUTER JOIN $PLAYLIST_MUSIC_ENTITY S " +
                "ON P.${PLAYLIST_ID_COLUMN} = S.${PLAYLIST_ID_COLUMN} GROUP BY P.$PLAYLIST_ID_COLUMN"
    )
    fun getPlaylistsInfoFlow(): Flow<List<PlaylistInfoWithNumberOfMusic>>

}