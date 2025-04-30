package com.sdu.composemusicplayer.data.queue

import androidx.room.Entity
import androidx.room.PrimaryKey

const val QUEUE_TABLE = "queue"

@Entity(QUEUE_TABLE)
data class QueueEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val songUri: String,

    val title: String,

    val artist: String?,

    val albumTitle: String?

)