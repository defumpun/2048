package com.example.a2048.domain.repo

import com.example.a2048.domain.model.GameState
import kotlinx.coroutines.flow.Flow


interface GameStateRepository {
    fun observe(): Flow<GameState?>
    suspend fun get(): GameState?
    suspend fun save(state: GameState)
}
