package com.example.a2048.data.di

import android.content.Context
import androidx.room.Room
import com.example.a2048.data.db.AppDatabase
import com.example.a2048.data.db.dao.GameStateDao
import com.example.a2048.data.db.dao.RecordsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideGameStateDao(db: AppDatabase): GameStateDao = db.gameStateDao()
    @Provides fun provideRecordsDao(db: AppDatabase): RecordsDao = db.recordsDao()
}