package com.example.a2048.domain.di

import com.example.a2048.domain.repo.GameStateRepository
import com.example.a2048.domain.repo.RecordsRepository
import com.example.a2048.domain.usecase.ObserveGameStateUseCase
import com.example.a2048.domain.usecase.SaveGameStateUseCase
import com.example.a2048.domain.usecase.AddRecordUseCase
import com.example.a2048.domain.usecase.GetGameStateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideObserveGameState(repo: GameStateRepository) =
        ObserveGameStateUseCase(repo)

    @Provides
    fun provideSaveGameState(repo: GameStateRepository) =
        SaveGameStateUseCase(repo)

    @Provides
    fun provideGetGameState(repo: GameStateRepository) =
        GetGameStateUseCase(repo)

    @Provides
    fun provideAddRecord(repo: RecordsRepository) =
        AddRecordUseCase(repo)
}
