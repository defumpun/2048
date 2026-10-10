package com.example.a2048.data.repo

import com.example.a2048.data.db.GameStateEntity
import com.example.a2048.data.db.dao.GameStateDao
import com.example.a2048.domain.model.GameState
import com.example.a2048.domain.repo.GameStateRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

class GameStateRepositoryImpl(
    private val dao: GameStateDao
) : GameStateRepository {

    override fun observe(): Flow<GameState?> =
        dao.observe().map { it?.toDomain() }

    override suspend fun get(): GameState? =
        dao.get()?.toDomain()

    override suspend fun save(state: GameState) {
        dao.upsert(state.toEntity())
    }
}

private fun GameStateEntity.toDomain() = GameState(
    size = size,
    matrix = matrix,
    highlightedCells = highlightedCells,
    score = score,
    gameOver = gameOver
)

private fun GameState.toEntity() = GameStateEntity(
    id = 1L,
    size = size,
    matrix = matrix,
    highlightedCells = highlightedCells,
    score = score,
    gameOver = gameOver
)
