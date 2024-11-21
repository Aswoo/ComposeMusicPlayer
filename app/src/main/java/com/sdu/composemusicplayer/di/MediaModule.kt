package com.sdu.composemusicplayer.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

/**
 * Module responsible for providing ExoPlayer instances to ViewModel components.
 */
@Module
@InstallIn(ServiceComponent::class) // ServiceScoped로 설정하여 MediaService에서 사용
class MediaModule {

    @Provides
    @ServiceScoped
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: Player // 이미 Player가 주입 가능한 상태라고 가정
    ): MediaSession {
        return MediaSession.Builder(context, player).build()
    }

//    @UnstableApi
//    @Provides
//    fun provideMediaNotificationManager(
//        context: Context,
//        sessionToken: SessionToken,
//        player: Player,
//        notificationListener: PlayerNotificationManager.NotificationListener
//    ): MediaNotificationManager {
//        return MediaNotificationManager(context, sessionToken, player, notificationListener)
//    }
}
