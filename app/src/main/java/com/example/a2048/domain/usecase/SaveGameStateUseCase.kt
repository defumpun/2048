package com.example.a2048.domain.usecase

import com.example.a2048.domain.model.GameState
import com.example.a2048.domain.repo.GameStateRepository

class SaveGameStateUseCase(
    private val repo: GameStateRepository
) {
    suspend operator fun invoke(state: GameState) = repo.save(state)
}
