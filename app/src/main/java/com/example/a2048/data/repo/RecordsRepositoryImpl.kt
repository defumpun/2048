package com.example.a2048.data.repo

import com.example.a2048.data.db.dao.RecordsDao
import com.example.a2048.data.db.RecordEntity
import com.example.a2048.domain.model.GameRecord
import com.example.a2048.domain.repo.RecordsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecordsRepositoryImpl(
    private val dao: RecordsDao
) : RecordsRepository {

    override fun observe(): Flow<List<GameRecord>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun clear() = dao.clear()

    override suspend fun add(score: Int, size: Int) {
        dao.insert(
            RecordEntity(
                score = score,
                size = size,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}

private fun RecordEntity.toDomain() = GameRecord(
    id = id,
    score = score,
    size = size,
    createdAt = createdAt
)
