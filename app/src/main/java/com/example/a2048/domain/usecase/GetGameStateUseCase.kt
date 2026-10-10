package com.example.a2048.domain.usecase

import com.example.a2048.domain.repo.GameStateRepository


class GetGameStateUseCase(
    private val repo: GameStateRepository
) {
    suspend operator fun invoke() = repo.get()
}
