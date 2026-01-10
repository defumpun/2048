package com.example.a2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a2048.ui.theme.TileColors.getBackgroundColor
import com.example.a2048.ui.theme.TileColors.getTextColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Game2048App()
            }
        }
    }
}

@Composable
fun Game2048App(viewModel: GameLogic = viewModel()) {
    val matrix by viewModel.matrix
    val gameOver = viewModel.gameOver.value
    val currentScore by viewModel.score
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    val widthSize = screenWidth / viewModel.size

    val coroutineScope = rememberCoroutineScope()
    var isSwipeLocked by remember { mutableStateOf(false) }
    if (gameOver) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(
                text = "Очки: $currentScore",
                fontSize = 32.sp
            )
            Spacer(Modifier.padding(16.dp))
            Button(onClick = {
                viewModel.resetGame()
            }) {
                Text("restart")
            }
        }
    } else (
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {  // Детектор свайпов по всему экрану
                        detectDragGestures()
                        { change, dragAmount ->
                            if (isSwipeLocked) return@detectDragGestures
                            change.consume()
                            val (dx, dy) = dragAmount
                            val threshold = 8f  // Минимальное смещение для свайпа (в пикселях)

                            val direction = when {
                                kotlin.math.abs(dx) > kotlin.math.abs(dy) -> {
                                    if (dx > threshold) Direction.RIGHT else if (dx < -threshold) Direction.LEFT else null
                                }

                                else -> {
                                    if (dy > threshold) Direction.DOWN else if (dy < -threshold) Direction.UP else null
                                }
                            }

                            direction?.let {
                                viewModel.move(it)
                                isSwipeLocked = true  // Блокируем свайпы
                                coroutineScope.launch {
                                    delay(200L)  // Задержка 300 мс
                                    isSwipeLocked = false  // Снимаем блокировку
                                }
                            }
                        }
                    }
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = {
                        viewModel.resetGame()
                    }) {
                        Text("Restart")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        viewModel.changeSize()
                    }) {
                        Text("сменить размер")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Очки: $currentScore",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Matrix(viewModel)
            }
            )
}

@Composable
fun Matrix(viewModel: GameLogic = viewModel()) {
    val matrix by viewModel.matrix
    val highlightedCellsState = remember { viewModel.highlightedCellsState }
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    val widthSize = screenWidth / viewModel.size
    Column {
        for (rowIndex in matrix.indices) {
            val row = matrix[rowIndex]
            Row {
                for (colIndex in row.indices) {
                    val cell = row[colIndex]
                    val isHighlighted =
                        highlightedCellsState.value.contains(Pair(rowIndex, colIndex))
                    Box(
                        modifier = Modifier
                            .size(widthSize.dp)
                            .background(
                                color = getBackgroundColor(cell)
                            )
                            .border(
                                width = if (isHighlighted) 4.dp else 0.1.dp,
                                color = if (isHighlighted) Color.Yellow else Color.Gray
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cell != 0) {
                            Text(
                                text = cell.toString(),
                                fontSize = (widthSize / 4).sp,
                                fontWeight = FontWeight.Bold,
                                color = getTextColor(cell)
                            )
                        }
                    }
                }
            }
        }
    }
}
