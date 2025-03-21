package com.sdu.composemusicplayer.network.data

import com.sdu.composemusicplayer.network.model.NetworkErrorException
import com.sdu.composemusicplayer.network.model.NotFoundException
import com.sdu.composemusicplayer.network.model.SongLyricsNetwork
import com.sdu.composemusicplayer.network.service.LyricsService
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LyricsSource @Inject constructor(
    private val lyricsService: LyricsService
) {


    suspend fun getSongLyrics(
        artistName: String,
        trackName: String,
        albumName: String,
        durationSeconds: Int,
    ): SongLyricsNetwork {
        return try {
            lyricsService.getSongLyrics(artistName, trackName, albumName, durationSeconds)
        } catch (e: HttpException) {
            if (e.code() == 404) throw NotFoundException("Lyrics not found")
            else throw NetworkErrorException(e.message())
        } catch (e: Exception) {
            throw NetworkErrorException(e.message ?: "Network error")
        }
    }


}