package com.example.a2048.data.di

import com.example.a2048.data.db.dao.GameStateDao
import com.example.a2048.data.db.dao.RecordsDao
import com.example.a2048.data.repo.GameStateRepositoryImpl
import com.example.a2048.data.repo.RecordsRepositoryImpl
import com.example.a2048.domain.repo.GameStateRepository
import com.example.a2048.domain.repo.RecordsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides @Singleton
    fun provideGameStateRepository(dao: GameStateDao): GameStateRepository =
        GameStateRepositoryImpl(dao)

    @Provides @Singleton
    fun provideRecordsRepository(dao: RecordsDao): RecordsRepository =
        RecordsRepositoryImpl(dao)
}
