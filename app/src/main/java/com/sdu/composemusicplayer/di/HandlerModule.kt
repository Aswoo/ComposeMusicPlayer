package com.sdu.composemusicplayer.di

import android.os.Handler
import android.os.Looper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HandlerModule {
    @Provides
    @Singleton
    fun provideMainHandler(): Handler {
        return Handler(Looper.getMainLooper())
    }
}
