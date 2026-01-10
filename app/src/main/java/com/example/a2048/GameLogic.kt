package com.example.a2048

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameLogic : ViewModel() {

    // Реактивное состояние для матрицы (MutableList<MutableList<Int>>)
    @SuppressLint("MutableCollectionMutableState")
    var size = 4

    fun changeSize() {
        if (size < 6) {
            this.size += 1
        } else this.size = 4
        resetGame()
    }

    private val _matrix = mutableStateOf(createMatrix(size))
    val matrix: androidx.compose.runtime.State<MutableList<MutableList<Int>>> = _matrix

    private val _highlightedCellsState = mutableStateOf<List<Pair<Int, Int>>>(emptyList())

    var highlightedCellsState: androidx.compose.runtime.State<List<Pair<Int, Int>>> =
        _highlightedCellsState


    // Реактивное состояние для счёта
    private val _score = mutableStateOf(0)
    val score: androidx.compose.runtime.State<Int> = _score  // Публичный доступ только для чтения

    // Реактивное состояние для gameover
    private var _gameOver = mutableStateOf(false)
    val gameOver: MutableState<Boolean> = _gameOver  // Публичный доступ только для чтения

    // Функция создания матрицы (теперь приватная, вызывается только внутри ViewModel)
    private fun createMatrix(size: Int): MutableList<MutableList<Int>> {
        require(size in 4..6) { "Размер матрицы должен быть от 4 до 6" }
        return MutableList(size) { MutableList(size) { 0 } }
    }

    init {
        addNumber()
        addNumber()
    }


    fun addNumber() {
        val zeros = mutableListOf<Pair<Int, Int>>()
        _highlightedCellsState.value = emptyList()
        for (i in _matrix.value.indices) {
            for (j in _matrix.value[i].indices) {
                if (_matrix.value[i][j] == 0) {
                    zeros.add(Pair(i, j))
                }
            }
        }
        if (zeros.isNotEmpty()) {
            val (i, j) = zeros[Random.Default.nextInt(zeros.size)]
            _matrix.value[i][j] = if (Random.Default.nextDouble() < 0.9) 2 else 411
            _highlightedCellsState.value = listOf(Pair(i, j))
        }
        checkGameOver()
    }

    private fun mergeRow(row: MutableList<Int>): Pair<MutableList<Int>, Int> {
        // 1. Удаляем нули и сдвигаем влево
        val shifted = row.filter { it != 0 }.toMutableList()
        while (shifted.size < row.size) {
            shifted.add(0)
        }

        // 2. Сливаем одинаковые числа и подсчитываем очки (слева направо)
        val merged = mutableListOf<Int>()
        var localScore = 0
        var i = 0
        while (i < shifted.size) {
            if (i < shifted.size - 1 && shifted[i] == shifted[i + 1] && shifted[i] != 0) {
                val newValue = shifted[i] * 2
                merged.add(newValue)
                localScore += newValue  // Очки равны новому значению
                i += 2  // Пропускаем следующий, так как он слит
            } else {
                merged.add(shifted[i])
                i++
            }
        }
        // Заполняем остаток нулями
        while (merged.size < row.size) {
            merged.add(0)
        }

        return Pair(merged, localScore)
    }


    fun transpose(matrix: List<List<Int>>): List<List<Int>> {
        val size = matrix.size
        return List(size) { i -> List(size) { j -> matrix[j][i] } }
    }


    fun move(direction: Direction): Boolean {
        val currentMatrix = _matrix.value.toMutableList()
        var moved = false
        var moveScore = 0

        when (direction) {
            Direction.LEFT -> {
                for (i in 0 until currentMatrix.size) {
                    val (newRow, rowScore) = mergeRow(currentMatrix[i])
                    moveScore += rowScore
                    if (newRow != currentMatrix[i]) {
                        currentMatrix[i] = newRow
                        moved = true
                    }
                }
            }

            Direction.RIGHT -> {
                for (i in 0 until currentMatrix.size) {
                    val reversedRow = currentMatrix[i].reversed().toMutableList()
                    val (merged, rowScore) = mergeRow(reversedRow)
                    moveScore += rowScore
                    val finalRow = merged.reversed().toMutableList()
                    if (finalRow != currentMatrix[i]) {
                        currentMatrix[i] = finalRow
                        moved = true
                    }
                }
            }

            Direction.UP -> {
                // Используем стиль из Compose: transpose(_matrix.value).map { it.toMutableList() }.toMutableList()
                val transposed = transpose(_matrix.value).map { it.toMutableList() }.toMutableList()
                for (i in 0 until transposed.size) {
                    val (newCol, colScore) = mergeRow(transposed[i])
                    moveScore += colScore
                    if (newCol != transposed[i]) {
                        transposed[i] = newCol
                        moved = true
                    }
                }
                // Транспонируем обратно в том же стиле
                val newMatrix = transpose(transposed).map { it.toMutableList() }.toMutableList()
                currentMatrix.clear()
                currentMatrix.addAll(newMatrix)
            }

            Direction.DOWN -> {
                // Аналогично UP, но с инверсией для "слияния вниз"
                val transposed = transpose(_matrix.value).map { it.toMutableList() }.toMutableList()
                for (i in 0 until transposed.size) {
                    val reversedCol = transposed[i].reversed().toMutableList()
                    val (merged, colScore) = mergeRow(reversedCol)
                    moveScore += colScore
                    val finalCol = merged.reversed().toMutableList()
                    if (finalCol != transposed[i]) {
                        transposed[i] = finalCol
                        moved = true
                    }
                }
                // Транспонируем обратно в том же стиле
                val newMatrix = transpose(transposed).map { it.toMutableList() }.toMutableList()
                currentMatrix.clear()
                currentMatrix.addAll(newMatrix)
            }
        }

        if (moved) {
            _matrix.value = currentMatrix
            _score.value += moveScore  // Счёт обновляется только при слиянии
            addNumber()
            return true
        }
        return false
    }


    fun resetGame() {
        _gameOver.value = false
        _matrix.value = createMatrix(size)
        _score.value = 0
        addNumber()
        addNumber()
    }

    // Проверка окончания игры
    private fun checkGameOver() {
        println("GAMEOVER = CHECK")
        // Проверка наличия пустых клеток
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (_matrix.value[i][j] == 0) {
                    return // Есть пустые клетки - игра продолжается
                }
            }
        }

        // Проверка возможности объединения
        for (i in 0 until size) {
            for (j in 0 until size - 1) {
                if (_matrix.value[i][j] == _matrix.value[i][j + 1]) {
                    return // Можно объединить по горизонтали
                }
            }
        }

        for (j in 0 until size) {
            for (i in 0 until size - 1) {
                if (_matrix.value[i][j] == _matrix.value[i + 1][j]) {
                    return // Можно объединить по вертикали
                }
            }
        }

        // Если ни одно условие не выполнилось - игра окончена
        _gameOver.value = true
        println("GAMEOVER = TRUE")
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
