@file:OptIn(UnstableApi::class)

package com.sdu.composemusicplayer.mediaPlayer.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayerServiceManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private var isServiceRunning = false

        @OptIn(UnstableApi::class)
        fun startMusicService() {
            if (!isServiceRunning) {
                val intent = Intent(context, MediaService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
                isServiceRunning = true
            }
        }

        @OptIn(UnstableApi::class)
        fun stopMusicService() {
            if (isServiceRunning) {
                val intent = Intent(context, MediaService::class.java)
                context.stopService(intent)
                isServiceRunning = false
            }
        }
    }
