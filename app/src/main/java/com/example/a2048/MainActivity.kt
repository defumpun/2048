package com.example.a2048

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a2048.presentation.game.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.auto(
                lightScrim = Color.Transparent.toArgb(),
                darkScrim = Color.Transparent.toArgb(),
            )
        )

        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Navigator.AppRoot()
            }
        }
    }
}

@Composable
fun GameLogicWithViewModel(
    viewModel: GameViewModel = hiltViewModel(),
    onOpenRecords: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Game2048App(
        matrix = state.matrix,
        gameOver = state.gameOver,
        currentScore = state.score,
        highlightedCells = state.highlightedCells,
        size = state.size,
        onRestart = viewModel::onRestart,
        onChangeSize = viewModel::onChangeSize,
        onMove = viewModel::onMove,
        onOpenRecords = onOpenRecords,
    )
}


@Composable
fun Game2048App(
    matrix: List<List<Int>>,
    gameOver: Boolean,
    currentScore: Int,
    size: Int,
    onRestart: () -> Unit,
    onChangeSize: () -> Unit,
    onMove: (Direction) -> Unit,
    highlightedCells: List<Pair<Int, Int>>,
    onOpenRecords: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    var isSwipeLocked by remember { mutableStateOf(false) }

    Image(
        painter = painterResource(id = R.drawable.bg2),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth(),
        contentScale = ContentScale.Crop
    )

    if (gameOver) {
        GameOverScreen2(
            currentScore = currentScore,
            onRestart = onRestart,
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag(TestTags.MAIN_SCREEN)
                .pointerInput(Unit) {
                    detectDragGestures()
                    { change, dragAmount ->
                        if (isSwipeLocked) return@detectDragGestures
                        change.consume()
                        val (dx, dy) = dragAmount
                        val threshold = 8f

                        val direction = when {
                            abs(dx) > abs(dy) -> {
                                if (dx > threshold) Direction.RIGHT else if (dx < -threshold) Direction.LEFT else null
                            }

                            else -> {
                                if (dy > threshold) Direction.DOWN else if (dy < -threshold) Direction.UP else null
                            }
                        }
                        direction?.let {
                            onMove(it)
                            isSwipeLocked = true
                            coroutineScope.launch {
                                delay(200L)
                                isSwipeLocked = false
                            }
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = {
                    onRestart()
                }, modifier = Modifier.testTag(TestTags.RESTART_BUTTON)) {
                    Text(stringResource(R.string.restart))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onChangeSize() },
                    modifier = Modifier.testTag(TestTags.CHANGE_SIZE_BUTTON)
                ) {
                    Text(stringResource(R.string.change_size))
                }
                Button(
                    onClick = { onOpenRecords() },
                    modifier = Modifier.testTag(TestTags.RECORD_BUTTON)
                ) {
                    Text(stringResource(R.string.record_text_button))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.points).format(currentScore),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            MatrixContent(
                matrix = matrix,
                highlightedCells = highlightedCells,
                size = size
            )
        }
    }
}

@Composable
fun MatrixContent(
    matrix: List<List<Int>>,
    highlightedCells: List<Pair<Int, Int>>,
    size: Int,
) {
    val highlightedCellsState = highlightedCells
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    val widthSize = screenWidth / size
    val numberToImageMap = mapOf(
        2 to R.drawable.ejevika,
        4 to R.drawable.arbuz,
        8 to R.drawable.grape,
        16 to R.drawable.arbuz,
        32 to R.drawable.ejevika,
        64 to R.drawable.arbuz,
        128 to R.drawable.arbuz,
        256 to R.drawable.arbuz,
        512 to R.drawable.arbuz,
        1024 to R.drawable.arbuz,
        2048 to R.drawable.arbuz
    )

    Column {
        for (rowIndex in matrix.indices) {
            val row = matrix[rowIndex]
            Row {
                for (colIndex in row.indices) {
                    val cell = row[colIndex]
                    val isHighlighted =
                        highlightedCellsState.contains(Pair(rowIndex, colIndex))
                    Box(
                        modifier = Modifier
                            .size(widthSize.dp)
                            .border(
                                width = if (isHighlighted) 4.dp else 0.1.dp,
                                color = if (isHighlighted) Color.Yellow else Color.Gray
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cell != 0) {
                            val imageRes = numberToImageMap[cell] ?: R.drawable.arbuz
                            Image(
                                painter = painterResource(imageRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = cell.toString(),
                                fontSize = (widthSize / 4).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameOverScreen2(
    currentScore: Int,
    onRestart: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag(TestTags.GAME_OVER_SCREEN),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                LayoutInflater.from(context)
                    .inflate(R.layout.game_over_screen_2, null)
                    .apply {
                        background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.window_bg
                            )
                    }
            },
            update = { view ->
                val context = view.context
                view.findViewById<TextView>(R.id.tvScore).text =
                    context.getString(
                        R.string.points,
                        currentScore
                    )
                view.findViewById<Button>(R.id.btnRestart)
                    .setOnClickListener {
                        onRestart()
                    }
            }
        )
    }
}

@Composable
fun RecordsScreen(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onBack() }
            .testTag(TestTags.RECORD_SCREEN),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.record_text),
            modifier = Modifier.testTag(TestTags.RECORD_TITLE),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
