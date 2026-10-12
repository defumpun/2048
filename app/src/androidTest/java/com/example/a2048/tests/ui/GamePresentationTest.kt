package com.example.a2048.tests.ui

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import com.example.a2048.*
import com.example.a2048.fixtures.*
import com.example.a2048.support.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed

class GamePresentationTest : BaseComponentTest() {
    private fun showGame(currentScore: Int = 0, onChangeSize: () -> Unit = {}, onRestart: () -> Unit = {}) {
        composeRule.setContent {
            Game2048App(
                matrix = GameFixtures.presentationMatrix(),
                gameOver = false,
                currentScore = currentScore,
                size = 4,
                onRestart = onRestart,
                onChangeSize = onChangeSize,
                onMove = {},
                highlightedCells = emptyList(),
                onOpenRecords = {},
            )
        }
    }

    @Test
    fun fruitTilesHaveAccessibleValues() {
        before {
            showGame()
        }.run {
            step("Проверка: значения фруктовых плиток доступны для чтения") {
                for (value in listOf(16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192)) {
                    checkUi { composeRule.onNodeWithContentDescription(value.toString()).assertExists() }
                }
                ui.saveSnapshot("fruit-preview.png")
            }
        }
    }

    @Test
    fun allButtonsFitInsideScreenWithoutOverlap() {
        before {
            showGame()
        }.run {
            step("Проверка: кнопки помещаются на экране и не перекрываются") {
                val screen = checkUi { composeRule.onRoot().fetchSemanticsNode().boundsInRoot }
                val buttons = listOf(TestTags.RESTART_BUTTON, TestTags.CHANGE_SIZE_BUTTON, TestTags.RECORD_BUTTON)
                    .map { checkUi { composeRule.onNodeWithTag(it).fetchSemanticsNode().boundsInRoot } }
                for (button in buttons) {
                    assertTrue("Button must fit within screen", button.left >= screen.left && button.right <= screen.right && button.top >= screen.top && button.bottom <= screen.bottom)
                    assertTrue("Touch target must be at least 48dp", button.height >= 48 * composeRule.density.density)
                }
                for (index in buttons.indices) {
                    for (other in index + 1 until buttons.size) {
                        val first = buttons[index]
                        val second = buttons[other]
                        assertTrue("Buttons must not overlap", first.right <= second.left || second.right <= first.left ||
                            first.bottom <= second.top || second.bottom <= first.top)
                    }
                }
            }
        }
    }

    @Test
    fun cancellingRestartKeepsCurrentGame() {
        var restarts = 0

        before {
            showGame(currentScore = 200, onRestart = { restarts++ })
        }.run {
            step("Действие: запросить перезапуск") {
                ui.game.restart()
            }
            step("Проверка: показано подтверждение, игра ещё не изменилась") {
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed() }
                checkUi { composeRule.runOnIdle { assertEquals(0, restarts) } }
            }
            step("Действие: отменить операцию") {
                ui.confirmation.cancel()
            }
            step("Проверка: состояние игры сохранено") {
                checkUi { composeRule.runOnIdle { assertEquals(0, restarts) } }
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist() }
                checkUi { composeRule.onNodeWithContentDescription("2048").assertExists() }
            }
        }
    }

    @Test
    fun confirmingRestartRestartsGame() {
        var restarts = 0

        before {
            showGame(currentScore = 200, onRestart = { restarts++ })
        }.run {
            step("Действие: запросить перезапуск") {
                ui.game.restart()
            }
            step("Проверка: показано подтверждение, игра ещё не изменилась") {
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed() }
                checkUi { composeRule.runOnIdle { assertEquals(0, restarts) } }
            }
            step("Действие: подтвердить операцию") {
                ui.confirmation.confirm()
            }
            step("Проверка: операция выполнена один раз") {
                checkUi { composeRule.runOnIdle { assertEquals(1, restarts) } }
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist() }
            }
        }
    }

    @Test
    fun zeroScoreRestartNeedsNoConfirmation() {
        var restarts = 0

        before {
            showGame(onRestart = { restarts++ })
        }.run {
            step("Действие: перезапустить игру") {
                ui.game.restart()
            }
            step("Проверка: операция выполнена без подтверждения") {
                checkUi { composeRule.runOnIdle { assertEquals(1, restarts) } }
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist() }
            }
        }
    }

    @Test
    fun cancellingBoardSizeKeepsCurrentGame() {
        var changes = 0

        before {
            showGame(currentScore = 200, onChangeSize = { changes++ })
        }.run {
            step("Действие: запросить изменение размера") {
                ui.game.changeSize()
            }
            step("Проверка: показано подтверждение, игра ещё не изменилась") {
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed() }
                checkUi { composeRule.runOnIdle { assertEquals(0, changes) } }
            }
            step("Действие: отменить операцию") {
                ui.confirmation.cancel()
            }
            step("Проверка: состояние игры сохранено") {
                checkUi { composeRule.runOnIdle { assertEquals(0, changes) } }
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist() }
                checkUi { composeRule.onNodeWithContentDescription("2048").assertExists() }
            }
        }
    }

    @Test
    fun confirmingBoardSizeChangesBoardSize() {
        var changes = 0

        before {
            showGame(currentScore = 200, onChangeSize = { changes++ })
        }.run {
            step("Действие: запросить изменение размера") {
                ui.game.changeSize()
            }
            step("Проверка: показано подтверждение, игра ещё не изменилась") {
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed() }
                checkUi { composeRule.runOnIdle { assertEquals(0, changes) } }
            }
            step("Действие: подтвердить операцию") {
                ui.confirmation.confirm()
            }
            step("Проверка: операция выполнена один раз") {
                checkUi { composeRule.runOnIdle { assertEquals(1, changes) } }
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist() }
            }
        }
    }

    @Test
    fun zeroScoreBoardSizeChangeNeedsNoConfirmation() {
        var changes = 0

        before {
            showGame(onChangeSize = { changes++ })
        }.run {
            step("Действие: сменить размер поля") {
                ui.game.changeSize()
            }
            step("Проверка: операция выполнена без подтверждения") {
                checkUi { composeRule.runOnIdle { assertEquals(1, changes) } }
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist() }
            }
        }
    }

}
