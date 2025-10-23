package com.sdu.composemusicplayer.presentation.musicPlayerSheet.album

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.key.Keyer
import coil.request.Options
import com.sdu.composemusicplayer.domain.model.MusicAlbumArtModel
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream

class AlbumKeyer : Keyer<MusicAlbumArtModel> {
    /**
     * Songs in the same album (should) have the same art work.
     * So we use the albumId as the key to use the same image
     * for all songs in the same album. If the song has no album, then use its uri as the key
     */
    override fun key(
        data: MusicAlbumArtModel,
        options: Options,
    ): String = data.albumId?.toString() ?: data.uri.toString()
}

class SongKeyer : Keyer<MusicAlbumArtModel> {
    override fun key(
        data: MusicAlbumArtModel,
        options: Options,
    ): String = data.uri.toString()
}

class AlbumArtFetcher(
    private val data: MusicAlbumArtModel,
    private val options: Options,
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        Log.d(
            "Keyer",
            "AlbumArtFetcher request: " +
                "$data\n" +
                "${options.size}",
        )

        return try {
            val metadataRetriever = MediaMetadataRetriever()
            
            // URI가 유효한지 확인
            if (data.uri == Uri.EMPTY || data.uri.toString().isEmpty()) {
                Log.d("Keyer", "Empty URI, returning null")
                return null
            }
            
            metadataRetriever.setDataSource(options.context, data.uri)
            val byteArr = metadataRetriever.embeddedPicture
            
            if (byteArr == null) {
                Log.d("Keyer", "No embedded picture found")
                return null
            }

            val bufferedSource = ByteArrayInputStream(byteArr).source().buffer()
            SourceResult(
                ImageSource(bufferedSource, options.context),
                "image/*",
                DataSource.MEMORY,
            )
        } catch (e: Exception) {
            Log.e("Keyer", "Error fetching album art: ${e.message}")
            null
        }
    }

    class Factory : Fetcher.Factory<MusicAlbumArtModel> {
        override fun create(
            data: MusicAlbumArtModel,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher {
            return AlbumArtFetcher(data, options)
        }
    }
}
