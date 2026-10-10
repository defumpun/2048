package com.example.a2048.domain.usecase

import com.example.a2048.domain.repo.RecordsRepository

class AddRecordUseCase(
    private val repo: RecordsRepository
) {
    suspend operator fun invoke(score: Int, size: Int) = repo.add(score, size)
}
