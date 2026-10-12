package com.example.a2048.domain.engine

import com.example.a2048.Direction
import com.example.a2048.domain.model.GameState
import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class GameEngineTest {
    private val engine = GameEngine(Random(0))
    private fun state(matrix: List<List<Int>>, score: Int = 0, gameOver: Boolean = false) =
        GameState(
            size = matrix.size,
            matrix = matrix,
            highlightedCells = emptyList(),
            score = score,
            gameOver = gameOver
        )

    @Test
    fun newGameHasTwoTilesForEverySupportedSize() {
        for (size in 4..6) {
            val game = engine.newGame(size)

            assertEquals(size, game.matrix.size)
            assertTrue(game.matrix.all { it.size == size })
            assertEquals(2, game.matrix.flatten().count { it != 0 })
            assertEquals(0, game.score)
            assertFalse(game.gameOver)
        }
    }

    @Test
    fun rejectsUnsupportedBoardSizes() {
        assertThrows(IllegalArgumentException::class.java) { engine.newGame(3) }
        assertThrows(IllegalArgumentException::class.java) { engine.newGame(7) }
    }

    @Test
    fun eachTileMergesOnlyOncePerMoveAndAddsMergedValuesToScore() {
        val game = state(listOf(listOf(2, 2, 2, 2)) + List(3) { List(4) { 0 } })
        val moved = engine.move(game, Direction.LEFT)

        assertEquals(listOf(4, 4), moved.matrix.first().take(2))
        assertEquals(8, moved.score)
    }

    @Test
    fun ineffectiveMoveDoesNotSpawnTileOrChangeScore() {
        val game = state(listOf(listOf(2, 0, 0, 0)) + List(3) { List(4) { 0 } }, score = 20)
        val moved = engine.move(game, Direction.LEFT)

        assertEquals(game, moved)
    }

    @Test
    fun finishedGameIgnoresFurtherMoves() {
        val game = state(List(4) { List(4) { 2 } }, score = 100, gameOver = true)
        for (direction in Direction.entries) {
            val moved = engine.move(game, direction)

            assertEquals(game, moved)
        }
    }

    @Test
    fun boardSizesCycleAndRestartResetsScoreKeepingSize() {
        val game = state(List(5) { List(5) { 0 } }, score = 100, gameOver = true)

        val nextSizes = (4..6).map(engine::nextSize)
        val restarted = engine.reset(game)

        assertEquals(listOf(5, 6, 4), nextSizes)
        assertEquals(5, restarted.size)
        assertEquals(0, restarted.score)
        assertFalse(restarted.gameOver)
        assertEquals(2, restarted.matrix.flatten().count { it != 0 })
    }
}
