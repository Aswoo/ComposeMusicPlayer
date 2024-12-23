package com.sdu.composemusicplayer.di

import com.sdu.composemusicplayer.viewmodel.IPlayerEnvironment
import com.sdu.composemusicplayer.viewmodel.PlayerEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnvironmentModule {
    @Provides
    @Singleton
    fun bindPlayerEnvironment(playerEnvironment: PlayerEnvironment): IPlayerEnvironment {
        return playerEnvironment
    }
}
