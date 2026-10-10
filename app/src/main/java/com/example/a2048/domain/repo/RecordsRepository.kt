package com.example.a2048.domain.repo

import com.example.a2048.domain.model.GameRecord
import kotlinx.coroutines.flow.Flow


interface RecordsRepository {
    fun observe(): Flow<List<GameRecord>>
    suspend fun add(score: Int, size: Int)
}
