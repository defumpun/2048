package com.example.a2048.domain.usecase

import com.example.a2048.domain.repo.RecordsRepository

class ClearRecordsUseCase(private val repository: RecordsRepository) {
    suspend operator fun invoke() = repository.clear()
}
