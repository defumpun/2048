package com.example.a2048.fixtures

import com.example.a2048.domain.model.GameState

object GameFixtures {
    fun presentationMatrix() = listOf(
        listOf(16, 32, 64, 128), listOf(256, 512, 1024, 2048),
        listOf(4096, 8192, 0, 0), List(4) { 0 }
    )

    fun finishedMatrix() = listOf(
        listOf(2, 4, 8, 16), listOf(32, 64, 128, 256),
        listOf(512, 1024, 2048, 4096), listOf(8192, 16384, 32768, 65536)
    )

    fun beforeLastMove(score: Int = 123) = GameState(
        size = 4,
        matrix = listOf(listOf(2, 0, 8, 16), listOf(32, 64, 128, 256),
            listOf(512, 1024, 2048, 4096), listOf(8192, 16384, 32768, 65536)),
        score = score, gameOver = false, highlightedCells = emptyList()
    )
}
