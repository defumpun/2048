package com.example.a2048.domain.model

data class GameState(
    val id: Long = 1L,          // один слот сохранения
    val size: Int,
    val matrix: List<List<Int>>,
    val highlightedCells: List<Pair<Int, Int>>,
    val score: Int,
    val gameOver: Boolean
)
