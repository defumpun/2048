package com.example.a2048.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a2048.data.db.GameStateEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface GameStateDao {

    @Query("SELECT * FROM game_state WHERE id = 1")
    fun observe(): Flow<GameStateEntity?>

    @Query("SELECT * FROM game_state WHERE id = 1")
    suspend fun get(): GameStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: GameStateEntity)

    @Query("DELETE FROM game_state")
    suspend fun clear()
}
