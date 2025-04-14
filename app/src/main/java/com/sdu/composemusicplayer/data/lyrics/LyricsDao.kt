package com.sdu.composemusicplayer.data.lyrics

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LyricsDao {

    @Insert
    suspend fun saveSongLyrics(lyricsEntity: LyricsEntity)

    @Query(
        "SELECT * FROM $LYRICS_TABLE " +
                "WHERE $LYRICS_TITLE_COLUMN = :title AND $LYRICS_ALBUM_COLUMN = :album " +
                "AND $LYRICS_ARTIST_COLUMN = :artist"
    )
    suspend fun getSongLyrics(
        title: String,
        album: String,
        artist: String
    ): LyricsEntity?

    @Query("DELETE FROM $LYRICS_TABLE")
    suspend fun deleteAll()

}