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

@HiltViewModel
class GameViewModel @Inject constructor(
    observeGameState: ObserveGameStateUseCase,
    private val getGameState: GetGameStateUseCase,
    private val saveGameState: SaveGameStateUseCase,
    private val addRecord: AddRecordUseCase,
    private val engine: GameEngine
) : ViewModel() {

    val state = observeGameState()
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            engine.newGame(size = 4) // дефолт если БД пустая
        )

    init {
        viewModelScope.launch {
            val existing = getGameState()
            if (existing == null) {
                saveGameState(engine.newGame(4))
            }
        }
    }

    fun onMove(direction: Direction) {
        viewModelScope.launch {
            val newState = engine.move(state.value, direction)
            saveGameState(newState)

            if (newState.gameOver) {
                addRecord(newState.score, newState.size)
            }
        }
    }

    fun onRestart() {
        viewModelScope.launch {
            val newState = engine.newGame(state.value.size)
            saveGameState(newState)
        }
    }

    fun onChangeSize() {
        viewModelScope.launch {
            val newSize = engine.nextSize(state.value.size)
            val newState = engine.newGame(newSize)
            saveGameState(newState)
        }
    }
}
