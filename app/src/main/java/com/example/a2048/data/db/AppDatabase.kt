package com.example.a2048.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.a2048.data.db.dao.GameStateDao
import com.example.a2048.data.db.dao.RecordsDao

@Database(
    entities = [GameStateEntity::class, RecordEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameStateDao(): GameStateDao
    abstract fun recordsDao(): RecordsDao
}
