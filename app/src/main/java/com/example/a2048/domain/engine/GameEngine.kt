package com.example.a2048.domain.engine

import com.example.a2048.Direction
import com.example.a2048.domain.model.GameState
import kotlin.math.abs
import kotlin.random.Random

class GameEngine(
    private val random: Random = Random.Default
) {

    fun nextSize(current: Int): Int = if (current < 6) current + 1 else 4

    fun newGame(size: Int): GameState {
        require(size in 4..6) { "Размер матрицы должен быть от 4 до 6" }

        val matrix = MutableList(size) { MutableList(size) { 0 } }
        var state = GameState(
            size = size,
            matrix = matrix.toImmutable(),
            highlightedCells = emptyList(),
            score = 0,
            gameOver = false
        )

        // как у тебя в init: addNumber() два раза
        state = addNumber(state)
        state = addNumber(state)
        return state
    }

    /**
     * Применить ход. Если ход не изменил матрицу — возвращаем исходный state.
     * Если изменил — обновляем матрицу, score, добавляем число, пересчитываем gameOver.
     */
    fun move(state: GameState, direction: Direction): GameState {
        if (state.gameOver) return state

        val size = state.size
        val currentMatrix = state.matrix.toMutableMatrix()

        var moved = false
        var moveScore = 0

        when (direction) {
            Direction.LEFT -> {
                for (i in 0 until size) {
                    val (newRow, rowScore) = mergeRow(currentMatrix[i])
                    moveScore += rowScore
                    if (newRow != currentMatrix[i]) {
                        currentMatrix[i] = newRow
                        moved = true
                    }
                }
            }

            Direction.RIGHT -> {
                for (i in 0 until size) {
                    val reversedRow = currentMatrix[i].asReversed().toMutableList()
                    val (merged, rowScore) = mergeRow(reversedRow)
                    moveScore += rowScore
                    val finalRow = merged.asReversed().toMutableList()
                    if (finalRow != currentMatrix[i]) {
                        currentMatrix[i] = finalRow
                        moved = true
                    }
                }
            }

            Direction.UP -> {
                val transposed = transpose(currentMatrix).map { it.toMutableList() }.toMutableList()
                for (i in 0 until size) {
                    val (newCol, colScore) = mergeRow(transposed[i])
                    moveScore += colScore
                    if (newCol != transposed[i]) {
                        transposed[i] = newCol
                        moved = true
                    }
                }
                val newMatrix = transpose(transposed).map { it.toMutableList() }.toMutableList()
                currentMatrix.clear()
                currentMatrix.addAll(newMatrix)
            }

            Direction.DOWN -> {
                val transposed = transpose(currentMatrix).map { it.toMutableList() }.toMutableList()
                for (i in 0 until size) {
                    val reversedCol = transposed[i].asReversed().toMutableList()
                    val (merged, colScore) = mergeRow(reversedCol)
                    moveScore += colScore
                    val finalCol = merged.asReversed().toMutableList()
                    if (finalCol != transposed[i]) {
                        transposed[i] = finalCol
                        moved = true
                    }
                }
                val newMatrix = transpose(transposed).map { it.toMutableList() }.toMutableList()
                currentMatrix.clear()
                currentMatrix.addAll(newMatrix)
            }
        }

        if (!moved) return state

        // обновляем state после движения
        var newState = state.copy(
            matrix = currentMatrix.toImmutable(),
            score = state.score + moveScore,
            highlightedCells = emptyList() // будет заполнено в addNumber
        )

        // добавляем число (как у тебя) и пересчитываем gameOver
        newState = addNumber(newState)
        newState = newState.copy(gameOver = checkGameOver(newState))

        return newState
    }

    fun reset(state: GameState): GameState = newGame(state.size)

    fun changeSizeAndReset(state: GameState): GameState = newGame(nextSize(state.size))

    // -------------------------
    // Внутренняя логика (перенос из твоего GameLogic)
    // -------------------------

    private fun addNumber(state: GameState): GameState {
        val matrix = state.matrix.toMutableMatrix()
        val zeros = mutableListOf<Pair<Int, Int>>()

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == 0) zeros.add(i to j)
            }
        }

        if (zeros.isEmpty()) {
            // некуда добавлять — просто пересчитаем gameOver снаружи
            return state.copy(
                highlightedCells = emptyList(),
                matrix = matrix.toImmutable()
            )
        }

        val (i, j) = zeros[random.nextInt(zeros.size)]
        matrix[i][j] = if (random.nextDouble() < 0.9) 2 else 4

        return state.copy(
            matrix = matrix.toImmutable(),
            highlightedCells = listOf(i to j)
        )
    }

    /**
     * 1) удаляем нули, сдвигаем влево
     * 2) сливаем одинаковые, считаем очки
     * 3) добиваем нулями
     */
    private fun mergeRow(row: MutableList<Int>): Pair<MutableList<Int>, Int> {
        val shifted = row.filter { it != 0 }.toMutableList()
        while (shifted.size < row.size) shifted.add(0)

        val merged = mutableListOf<Int>()
        var localScore = 0
        var i = 0
        while (i < shifted.size) {
            if (i < shifted.size - 1 && shifted[i] == shifted[i + 1] && shifted[i] != 0) {
                val newValue = shifted[i] * 2
                merged.add(newValue)
                localScore += newValue
                i += 2
            } else {
                merged.add(shifted[i])
                i++
            }
        }
        while (merged.size < row.size) merged.add(0)

        return merged to localScore
    }

    private fun transpose(matrix: List<List<Int>>): List<List<Int>> {
        val size = matrix.size
        return List(size) { i -> List(size) { j -> matrix[j][i] } }
    }

    private fun checkGameOver(state: GameState): Boolean {
        val size = state.size
        val matrix = state.matrix

        // Есть пустые клетки?
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (matrix[i][j] == 0) return false
            }
        }

        // Можно объединить по горизонтали?
        for (i in 0 until size) {
            for (j in 0 until size - 1) {
                if (matrix[i][j] == matrix[i][j + 1]) return false
            }
        }

        // Можно объединить по вертикали?
        for (j in 0 until size) {
            for (i in 0 until size - 1) {
                if (matrix[i][j] == matrix[i + 1][j]) return false
            }
        }

        return true
    }
}

// -------------------------
// Extensions для копирования матрицы
// -------------------------

private fun List<List<Int>>.toMutableMatrix(): MutableList<MutableList<Int>> =
    this.map { it.toMutableList() }.toMutableList()

private fun MutableList<MutableList<Int>>.toImmutable(): List<List<Int>> =
    this.map { it.toList() }
