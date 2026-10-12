package com.example.a2048.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a2048.Direction
import com.example.a2048.domain.engine.GameEngine
import com.example.a2048.domain.usecase.AddRecordUseCase
import com.example.a2048.domain.usecase.GetGameStateUseCase
import com.example.a2048.domain.usecase.ObserveGameStateUseCase
import com.example.a2048.domain.usecase.SaveGameStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@HiltViewModel
class GameViewModel @Inject constructor(
    observeGameState: ObserveGameStateUseCase,
    private val getGameState: GetGameStateUseCase,
    private val saveGameState: SaveGameStateUseCase,
    private val addRecord: AddRecordUseCase,
    private val engine: GameEngine
) : ViewModel() {
    private val actionsMutex = Mutex()

    val state = observeGameState()
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            engine.newGame(size = 4) // дефолт если БД пустая
        )

    init {
        viewModelScope.launch {
            actionsMutex.withLock {
                if (getGameState() == null) {
                    saveGameState(engine.newGame(4))
                }
            }
        }
    }

    fun onMove(direction: Direction) {
        viewModelScope.launch {
            actionsMutex.withLock {
                val previous = getGameState() ?: state.value
                if (previous.gameOver) return@withLock
                val newState = engine.move(previous, direction)
                saveGameState(newState)
                if (newState.gameOver) {
                    addRecord(newState.score, newState.size)
                }
            }
        }
    }

    fun onRestart() {
        viewModelScope.launch {
            actionsMutex.withLock {
                val current = getGameState() ?: state.value
                saveGameState(engine.newGame(current.size))
            }
        }
    }

    fun onChangeSize() {
        viewModelScope.launch {
            actionsMutex.withLock {
                val current = getGameState() ?: state.value
                saveGameState(engine.newGame(engine.nextSize(current.size)))
            }
        }
    }
}
