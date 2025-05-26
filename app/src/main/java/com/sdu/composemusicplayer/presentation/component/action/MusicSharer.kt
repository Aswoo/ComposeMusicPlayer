package com.sdu.composemusicplayer.presentation.component.action

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sdu.composemusicplayer.domain.model.Music


object MusicSharer : MusicShareAction {

    override fun share(context: Context, music: List<Music>) {
        if (music.isEmpty()) return
        if (music.size == 1) shareSingleSong(context, music[0])
        else shareMultipleSongs(context, music)
    }

}

fun shareSongs(context: Context, music: List<Music>) {
    if (music.isEmpty()) return
    if (music.size == 1) shareSingleSong(context, music[0])
    else shareMultipleSongs(context, music)
}


fun shareSingleSong(context: Context, music: Music) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, Uri.parse(music.audioPath))
        type = "audio/*"
    }
    val chooser = Intent.createChooser(intent, "Share ${music.title}")
    context.startActivity(chooser)
}

fun shareMultipleSongs(context: Context, music: List<Music>) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        val uris = music.map { Uri.parse(it.audioPath) }
        type = "audio/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
    }
    val chooser =
        Intent.createChooser(
            intent,
            "Share ${music[0].title} and ${music.size - 1} other files"
        )
    context.startActivity(chooser)
}