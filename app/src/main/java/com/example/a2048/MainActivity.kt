package com.example.a2048

import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a2048.presentation.game.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ln

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
    var showRestartConfirmation by rememberSaveable { mutableStateOf(false) }
    var showFieldConfirmation by rememberSaveable { mutableStateOf(false) }
    var resultDismissed by rememberSaveable(gameOver) { mutableStateOf(false) }
    val requestRestart = {
        if (gameOver || currentScore == 0) onRestart() else showRestartConfirmation = true
    }
    val requestChangeSize = {
        if (currentScore == 0) onChangeSize() else showFieldConfirmation = true
    }
    BackHandler(enabled = gameOver && !resultDismissed) { resultDismissed = true }

    Box(Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.bg2),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp)
                .testTag(TestTags.MAIN_SCREEN)
                .pointerInput(gameOver) {
                    detectDragGestures()
                    { change, dragAmount ->
                        if (isSwipeLocked || gameOver) return@detectDragGestures
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
        ) {
            val isWide = maxWidth > maxHeight
            val controlsWidth = (maxWidth * 0.32f).coerceIn(132.dp, 180.dp)
            if (isWide) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(
                        Modifier.width(controlsWidth).fillMaxHeight().verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ScreenHeader(stringResource(R.string.app_name))
                        GameControls(requestRestart, requestChangeSize, onOpenRecords, vertical = true)
                        GameScore(currentScore, compact = true)
                    }
                    AdaptiveBoard(matrix, highlightedCells, size, gameOver && !resultDismissed,
                        Modifier.weight(1f).fillMaxHeight())
                }
            } else {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ScreenHeader(stringResource(R.string.app_name))
                    GameControls(requestRestart, requestChangeSize, onOpenRecords)
                    GameScore(currentScore)
                    AdaptiveBoard(matrix, highlightedCells, size, gameOver && !resultDismissed,
                        Modifier.fillMaxWidth().weight(1f))
                }
            }
        }
        if (gameOver && !resultDismissed) {
            GameOverOverlay(
                currentScore = currentScore,
                onRestart = requestRestart,
                onOpenRecords = { resultDismissed = true; onOpenRecords() },
                onDismiss = { resultDismissed = true }
            )
        }
        if (showRestartConfirmation) {
            ConfirmationDialog(
                question = stringResource(R.string.confirm_restart),
                detail = stringResource(R.string.restart_detail),
                onConfirm = {
                    showRestartConfirmation = false
                    onRestart()
                },
                onDismiss = { showRestartConfirmation = false }
            )
        }
        if (showFieldConfirmation) {
            ConfirmationDialog(
                question = stringResource(R.string.confirm_change_size),
                detail = stringResource(R.string.change_size_detail),
                onConfirm = {
                    showFieldConfirmation = false
                    onChangeSize()
                },
                onDismiss = { showFieldConfirmation = false }
            )
        }
    }
}

@Composable
private fun GameControls(onRestart: () -> Unit, onChangeSize: () -> Unit, onOpenRecords: () -> Unit,
    vertical: Boolean = false) {
    if (vertical) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            CompactActionButton(stringResource(R.string.restart), onRestart, Modifier.fillMaxWidth().testTag(TestTags.RESTART_BUTTON))
            CompactActionButton(stringResource(R.string.change_size), onChangeSize, Modifier.fillMaxWidth().testTag(TestTags.CHANGE_SIZE_BUTTON))
            CompactActionButton(stringResource(R.string.record_text_button), onOpenRecords, Modifier.fillMaxWidth().testTag(TestTags.RECORD_BUTTON))
        }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactActionButton(stringResource(R.string.restart), onRestart, Modifier.weight(1f).testTag(TestTags.RESTART_BUTTON))
            CompactActionButton(stringResource(R.string.change_size), onChangeSize, Modifier.weight(1f).testTag(TestTags.CHANGE_SIZE_BUTTON))
            CompactActionButton(stringResource(R.string.record_text_button), onOpenRecords, Modifier.weight(1f).testTag(TestTags.RECORD_BUTTON))
        }
    }
}

@Composable
private fun GameScore(score: Int, compact: Boolean = false) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.points, score), fontSize = if (compact) 18.sp else 22.sp,
            fontWeight = FontWeight.Bold, color = Color.White,
            modifier = Modifier.background(Color(0xBB173D32), RoundedCornerShape(50))
                .padding(horizontal = if (compact) 10.dp else 20.dp, vertical = if (compact) 4.dp else 8.dp)
        )
    }
}

@Composable
private fun AdaptiveBoard(matrix: List<List<Int>>, highlightedCells: List<Pair<Int, Int>>, size: Int,
    frosted: Boolean, modifier: Modifier) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        MatrixContent(matrix, highlightedCells, size, minOf(maxWidth, maxHeight), frosted)
    }
}

@Composable
fun MatrixContent(
    matrix: List<List<Int>>,
    highlightedCells: List<Pair<Int, Int>>,
    size: Int,
    boardSize: Dp,
    frosted: Boolean = false,
) {
    val cellSize = ((boardSize.value - 12 - (size - 1) * 4) / size).coerceAtLeast(1f)
    val numberToImageMap = mapOf(
        2 to R.drawable.fruit_blackberry,
        4 to R.drawable.fruit_watermelon,
        8 to R.drawable.fruit_grape,
        16 to R.drawable.fruit_strawberry,
        32 to R.drawable.fruit_orange,
        64 to R.drawable.fruit_apple,
        128 to R.drawable.fruit_pear,
        256 to R.drawable.fruit_peach,
        512 to R.drawable.fruit_pineapple,
        1024 to R.drawable.fruit_coconut,
        2048 to R.drawable.fruit_dragon,
    )

    Column(
        modifier = Modifier.size(boardSize).testTag(TestTags.GAME_BOARD)
            .then(if (frosted) Modifier.blur(10.dp, BlurredEdgeTreatment.Unbounded) else Modifier)
            .drawWithContent {
                drawContent()
                // Uniform RenderEffect blur is available from Android 12.
                if (frosted && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    drawRect(Color(0x80485E50))
                }
            }
            .background(Color(0xB3173D32), RoundedCornerShape(20.dp))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (rowIndex in matrix.indices) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (colIndex in matrix[rowIndex].indices) {
                    val cell = matrix[rowIndex][colIndex]
                    val isHighlighted = highlightedCells.contains(rowIndex to colIndex)
                    val shape = RoundedCornerShape(12.dp)
                    Box(
                        modifier = Modifier
                            .size(cellSize.dp)
                            .background(
                                if (cell >= 2048) Color(0x664D3B0B) else Color(0x553B6A50),
                                shape
                            )
                            .border(
                                width = if (isHighlighted || cell >= 2048) 2.dp else 1.dp,
                                color = when {
                                    isHighlighted -> Color(0xFFE9FF83)
                                    cell >= 2048 -> Color(0xFFFFD166)
                                    else -> Color(0x337FCB94)
                                },
                                shape = shape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cell != 0) {
                            val tier = (ln(cell.toDouble()) / ln(2.0)).toFloat()
                            val imageFraction = (0.60f + tier * 0.03f).coerceAtMost(0.94f)
                            Image(
                                painter = painterResource(numberToImageMap[cell] ?: R.drawable.fruit_mango),
                                contentDescription = cell.toString(),
                                modifier = Modifier.fillMaxSize(imageFraction),
                                contentScale = ContentScale.Fit
                            )
                            Text(
                                text = cell.toString(),
                                fontSize = (cellSize / 5).coerceIn(10f, 16f).sp,
                                fontWeight = FontWeight.Bold,
                                color = if (cell >= 2048) Color(0xFFFFD166) else Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 3.dp)
                                    .background(Color(0xCC173D32), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
