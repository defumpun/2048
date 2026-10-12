package com.example.a2048.domain.usecase

import com.example.a2048.domain.repo.RecordsRepository

class ObserveRecordsUseCase(private val repository: RecordsRepository) {
    operator fun invoke() = repository.observe()
}
