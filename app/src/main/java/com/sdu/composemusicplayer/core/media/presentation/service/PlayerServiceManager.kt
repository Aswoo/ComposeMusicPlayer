@file:OptIn(UnstableApi::class)

package com.sdu.composemusicplayer.core.media.presentation.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
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
            Log.d("PlayerServiceManager", "startMusicService 호출됨")
            val intent = Intent(context, MediaService::class.java)
            Log.d("PlayerServiceManager", "MediaService 시작 시도: $intent")
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                    Log.d("PlayerServiceManager", "startForegroundService 성공")
                } else {
                    context.startService(intent)
                    Log.d("PlayerServiceManager", "startService 성공")
                }
                isServiceRunning = true
                Log.d("PlayerServiceManager", "MediaService 시작 완료")
            } catch (e: Exception) {
                Log.e("PlayerServiceManager", "MediaService 시작 실패", e)
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
