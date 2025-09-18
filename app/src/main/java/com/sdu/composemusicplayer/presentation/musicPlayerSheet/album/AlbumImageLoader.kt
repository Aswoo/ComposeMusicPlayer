@file:Suppress("ThrowingExceptionsWithoutMessageOrCause")
package com.sdu.composemusicplayer.presentation.musicPlayerSheet.album

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import coil.ImageLoader
import kotlinx.coroutines.Dispatchers

private const val PARALLELISM_LEVEL = 5

fun Context.efficientAlbumArtImageLoader() =
    ImageLoader
        .Builder(this)
        .components {
            add(AlbumKeyer())
            add(AlbumArtFetcher.Factory())
        }.build()

fun Context.inefficientAlbumArtImageLoader() =
    ImageLoader
        .Builder(this)
        .dispatcher(Dispatchers.IO.limitedParallelism(PARALLELISM_LEVEL))
        .components {
            add(SongKeyer())
            add(AlbumArtFetcher.Factory())
        }.build()

val LocalEfficientThumbnailImageLoader =
    staticCompositionLocalOf<ImageLoader> { throw IllegalStateException() }
val LocalInefficientThumbnailImageLoader =
    staticCompositionLocalOf<ImageLoader> { throw IllegalStateException() }
