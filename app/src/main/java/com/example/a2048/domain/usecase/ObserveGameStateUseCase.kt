package com.example.a2048.domain.usecase

import com.example.a2048.domain.repo.GameStateRepository

class ObserveGameStateUseCase(
    private val repo: GameStateRepository
) {
    operator fun invoke() = repo.observe()
}
