package com.example.a2048.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Long = 1L,
    val size: Int,
    val matrix: List<List<Int>>,
    val highlightedCells: List<Pair<Int, Int>>,
    val score: Int,
    val gameOver: Boolean
)