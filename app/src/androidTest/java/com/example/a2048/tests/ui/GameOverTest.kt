package com.example.a2048.tests.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.a2048.*
import com.example.a2048.data.db.AppDatabase
import com.example.a2048.data.repo.GameStateRepositoryImpl
import com.example.a2048.data.repo.RecordsRepositoryImpl
import com.example.a2048.domain.engine.GameEngine
import com.example.a2048.domain.usecase.AddRecordUseCase
import com.example.a2048.domain.usecase.GetGameStateUseCase
import com.example.a2048.domain.usecase.ObserveGameStateUseCase
import com.example.a2048.domain.usecase.SaveGameStateUseCase
import com.example.a2048.fixtures.*
import com.example.a2048.presentation.game.GameViewModel
import com.example.a2048.support.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameOverTest : BaseComponentTest() {
    private lateinit var database: AppDatabase
    private lateinit var game: GameStateRepositoryImpl
    private lateinit var records: RecordsRepositoryImpl
    private lateinit var viewModel: GameViewModel

    private fun showFinishedGame(onMove: (Direction) -> Unit = {}, onRestart: () -> Unit = {}) {
        composeRule.setContent {
            Game2048App(
                matrix = GameFixtures.finishedMatrix(),
                highlightedCells = emptyList(), size = 4, gameOver = true, currentScore = 123,
                onMove = onMove, onChangeSize = {}, onOpenRecords = {}, onRestart = onRestart
            )
        }
    }

    @Test
    fun finishedGameShowsResultAndScore() {
        before {
            showFinishedGame()
        }.run {
            step("Проверка: карточка результата содержит счёт и сохраняет поле") {
                checkUi { composeRule.onNodeWithTag(TestTags.GAME_OVER_SCREEN).assertIsDisplayed() }
                checkUi {
                    composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.dismiss_result))
                        .assertDoesNotExist()
                }
                checkUi { composeRule.onNodeWithTag(TestTags.MAIN_SCREEN).assertExists() }
                checkUi { composeRule.onNodeWithContentDescription("2048").assertExists() }
                checkUi {
                    composeRule.onNodeWithTag(TestTags.GAME_OVER_SCORE)
                        .assertTextEquals(composeRule.activity.getString(R.string.points, 123))
                }
                ui.saveSnapshot("game-over-preview.png")
            }
        }
    }

    @Test
    fun finishedGameRestartsImmediatelyWithoutConfirmation() {
        var restarts = 0

        before {
            showFinishedGame(onRestart = { restarts++ })
        }.run {
            step("Действие: перезапустить завершённую игру") {
                ui.tap(TestTags.GAME_OVER_RESTART)
            }
            step("Проверка: перезапуск вызван один раз без подтверждения") {
                checkUi { composeRule.runOnIdle { assertEquals(1, restarts) } }
                checkUi {
                    composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist()
                }
            }
        }
    }

    @Test
    fun tappingResultCardKeepsItOpen() {
        before {
            showFinishedGame()
        }.run {
            step("Действие: нажать на карточку результата") {
                composeRule.onNodeWithTag(TestTags.GAME_OVER_SCREEN)
                    .performTouchInput { click(Offset(20f, 20f)) }
            }
            step("Проверка: карточка осталась открытой") {
                checkUi { composeRule.onNodeWithTag(TestTags.GAME_OVER_SCREEN).assertIsDisplayed() }
            }
        }
    }

    @Test
    fun dismissedResultDoesNotAllowFurtherMoves() {
        var restarts = 0
        var moves = 0

        before {
            showFinishedGame(onMove = { moves++ }, onRestart = { restarts++ })
        }.run {
            step("Действие: нажать вне карточки результата") {
                composeRule.onNodeWithTag(TestTags.GAME_OVER_BACKDROP)
                    .performTouchInput { click(Offset(8f, 8f)) }
            }
            step("Проверка: закрыта только карточка результата") {
                checkUi {
                    composeRule.onNodeWithTag(TestTags.GAME_OVER_SCREEN).assertDoesNotExist()
                }
                checkUi { composeRule.onNodeWithTag(TestTags.MAIN_SCREEN).assertIsDisplayed() }
                checkUi { composeRule.onNodeWithContentDescription("2048").assertExists() }
                checkUi {
                    composeRule.runOnIdle {
                        assertEquals(0, moves); assertEquals(
                        0,
                        restarts
                    )
                    }
                }
            }
            step("Действие: попробовать сделать ход") {
                composeRule.onNodeWithTag(TestTags.MAIN_SCREEN).performTouchInput { swipeLeft() }
            }
            step("Проверка: завершённая игра не принимает ходы") {
                checkUi {
                    composeRule.runOnIdle {
                        assertEquals(0, moves); assertEquals(
                        0,
                        restarts
                    )
                    }
                }
            }
        }
    }

    @Test
    fun dismissedResultAllowsRestartWithoutConfirmation() {
        var restarts = 0
        var moves = 0

        before {
            showFinishedGame(onMove = { moves++ }, onRestart = { restarts++ })
        }.run {
            step("Действие: нажать вне карточки результата") {
                composeRule.onNodeWithTag(TestTags.GAME_OVER_BACKDROP)
                    .performTouchInput { click(Offset(8f, 8f)) }
            }
            step("Проверка: закрыта только карточка результата") {
                checkUi {
                    composeRule.onNodeWithTag(TestTags.GAME_OVER_SCREEN).assertDoesNotExist()
                }
                checkUi { composeRule.onNodeWithTag(TestTags.MAIN_SCREEN).assertIsDisplayed() }
                checkUi { composeRule.onNodeWithContentDescription("2048").assertExists() }
                checkUi {
                    composeRule.runOnIdle {
                        assertEquals(0, moves); assertEquals(
                        0,
                        restarts
                    )
                    }
                }
            }
            step("Действие: перезапустить игру") {
                ui.game.restart()
            }
            step("Проверка: перезапуск выполнен без подтверждения") {
                checkUi {
                    composeRule.runOnIdle {
                        assertEquals(0, moves); assertEquals(
                        1,
                        restarts
                    )
                    }
                }
                checkUi {
                    composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist()
                }
            }
        }
    }

    @Test
    fun dismissingResultAfterLastMoveStillSavesRecordInDatabase() {
        before {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            game = GameStateRepositoryImpl(database.gameStateDao())
            records = RecordsRepositoryImpl(database.recordsDao())
            runBlocking {
                game.save(GameFixtures.beforeLastMove())
                viewModel = withContext(Dispatchers.Main) {
                    GameViewModel(
                        ObserveGameStateUseCase(game), GetGameStateUseCase(game),
                        SaveGameStateUseCase(game), AddRecordUseCase(records), GameEngine()
                    )
                }
                withTimeout(5_000) { viewModel.state.first { it.score == 123 } }
            }
            composeRule.setContent {
                GameLogicWithViewModel(
                    viewModel = viewModel,
                    onOpenRecords = {})
            }
        }.after {
            if (::viewModel.isInitialized) composeRule.runOnIdle { viewModel.viewModelScope.cancel() }
            if (::database.isInitialized) database.close()
        }.run {
            step("Действие: сделать последний ход") {
                runBlocking { withContext(Dispatchers.Main) { viewModel.onMove(Direction.LEFT) } }
            }
            step("Проверка: показан результат завершённой игры") {
                checkUi { composeRule.onNodeWithTag(TestTags.GAME_OVER_SCREEN).assertIsDisplayed() }
            }
            step("Действие: закрыть результат нажатием вне карточки") {
                composeRule.onNodeWithTag(TestTags.GAME_OVER_BACKDROP)
                    .performTouchInput { click(Offset(8f, 8f)) }
            }
            step("Проверка: результат закрыт, игра завершена и рекорд сохранён") {
                checkUi {
                    composeRule.onNodeWithTag(TestTags.GAME_OVER_SCREEN).assertDoesNotExist()
                }
                checkUi { composeRule.onNodeWithTag(TestTags.MAIN_SCREEN).assertIsDisplayed() }
                checkUi { composeRule.onNodeWithContentDescription("2048").assertExists() }
                runBlocking {
                    val saved = withTimeout(5_000) { records.observe().first { it.isNotEmpty() } }
                    assertEquals(1, saved.size)
                    assertEquals(123, saved.single().score)
                    assertTrue(game.get()!!.gameOver)
                }
            }
        }
    }

}
