package com.example.a2048.tests.ui

import android.content.pm.ActivityInfo
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import com.example.a2048.*
import com.example.a2048.fixtures.*
import com.example.a2048.support.*
import com.example.a2048.scenarios.PrepareLargeBoardScenario
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveLayoutTest : BaseAppTest() {
    private val largeBoard by lazy { PrepareLargeBoardScenario(composeRule, ui) }

    @Test
    fun controlsStayAtSameLevelAcrossGameAndRecords() {
        lateinit var gameButtons: List<Rect>

        before {
            rotateTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            ui.game.assertDisplayed()
            gameButtons = listOf(TestTags.RESTART_BUTTON, TestTags.CHANGE_SIZE_BUTTON, TestTags.RECORD_BUTTON)
                .map { checkUi { composeRule.onNodeWithTag(it).assertIsDisplayed().fetchSemanticsNode().boundsInRoot } }
        }.after {
            restorePortrait()
        }.run {
            step("Действие: открыть рекорды") {
                ui.game.openRecords()
            }
            step("Проверка: вкладки рекордов выровнены с игровыми кнопками") {
                checkUi { composeRule.onNodeWithTag(TestTags.RECORD_SCREEN).assertIsDisplayed() }
                for (size in 4..6) {
                    val tab = checkUi {
                        composeRule.onNodeWithText(composeRule.activity.getString(R.string.board_size_tab, size))
                            .assertIsDisplayed().fetchSemanticsNode().boundsInRoot
                    }
                    assertTrue("Buttons must stay at the same level", abs(tab.center.y - gameButtons.first().center.y) < composeRule.density.density)
                }
            }
            step("Действие: вернуться в игру") {
                ui.records.goBack()
            }
            step("Проверка: кнопки игры сохранили расположение") {
                ui.game.assertDisplayed()
                val returned = checkUi { composeRule.onNodeWithTag(TestTags.RECORD_BUTTON).fetchSemanticsNode().boundsInRoot }
                assertEquals(gameButtons.last().top, returned.top, composeRule.density.density)
            }
        }
    }

    @Test
    fun largeBoardAndControlsFitInLandscape() {
        before {}.after {
            largeBoard.close()
            restorePortrait()
        }.run {
            scenario(largeBoard)
            step("Действие: повернуть устройство в альбомную ориентацию") {
                rotateTo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }
            step("Проверка: поле читаемо, все кнопки доступны и выровнены") {
                ui.game.assertDisplayed()
                val screen = checkUi { composeRule.onRoot().fetchSemanticsNode().boundsInRoot }
                val board = checkUi { composeRule.onNodeWithTag(TestTags.GAME_BOARD).assertIsDisplayed().fetchSemanticsNode().boundsInRoot }
                assertTrue("Board must remain readable in landscape", board.width >= screen.height * 0.72f)
                assertTrue("Board must be square", abs(board.width - board.height) < composeRule.density.density)
                assertTrue("Board must fit on screen", board.left >= screen.left && board.right <= screen.right && board.top >= screen.top && board.bottom <= screen.bottom)
                val buttons = listOf(TestTags.RESTART_BUTTON, TestTags.CHANGE_SIZE_BUTTON, TestTags.RECORD_BUTTON)
                    .map { checkUi { composeRule.onNodeWithTag(it).assertIsDisplayed().fetchSemanticsNode().boundsInRoot } }
                buttons.zipWithNext().forEach { (upper, lower) ->
                    assertTrue("Landscape buttons must be stacked vertically", upper.bottom <= lower.top)
                    assertTrue("Landscape buttons must align horizontally", abs(upper.center.x - lower.center.x) < composeRule.density.density)
                }
                ui.saveSnapshot("landscape-preview.png")
            }
        }
    }

    @Test
    fun landscapeRecordsRoundTripKeepsGameStateAndButtonPositions() {
        lateinit var gameButtons: List<Rect>

        before {}.after {
            largeBoard.close()
            restorePortrait()
        }.run {
            scenario(largeBoard)
            step("Подготовка: повернуть устройство и сохранить расположение кнопок") {
                rotateTo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                gameButtons = listOf(TestTags.RESTART_BUTTON, TestTags.CHANGE_SIZE_BUTTON, TestTags.RECORD_BUTTON)
                    .map { checkUi { composeRule.onNodeWithTag(it).assertIsDisplayed().fetchSemanticsNode().boundsInRoot } }
            }
            step("Действие: открыть рекорды") {
                ui.game.openRecords()
            }
            step("Проверка: экран рекордов и все вкладки отображаются") {
                checkUi { composeRule.onNodeWithTag(TestTags.RECORD_SCREEN).assertIsDisplayed() }
                for (size in 4..6) {
                    checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.board_size_tab, size)).assertIsDisplayed() }
                }
            }
            step("Действие: вернуться в игру") {
                ui.records.goBack()
            }
            step("Проверка: расположение кнопок и состояние игры сохранены") {
                ui.game.assertDisplayed()
                val returned = checkUi { composeRule.onNodeWithTag(TestTags.RECORD_BUTTON).fetchSemanticsNode().boundsInRoot }
                assertEquals(gameButtons.last().top, returned.top, composeRule.density.density)
                assertEquals(largeBoard.stateBeforeRotation, largeBoard.currentState())
            }
        }
    }

    @Test
    fun rotatingLargeBoardBackToPortraitKeepsState() {
        before {}.after {
            largeBoard.close()
            restorePortrait()
        }.run {
            scenario(largeBoard)
            step("Действие: повернуть устройство в альбомную ориентацию") {
                rotateTo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }
            step("Проверка: игра доступна, состояние сохранено") {
                ui.game.assertDisplayed()
                assertEquals(largeBoard.stateBeforeRotation, largeBoard.currentState())
            }
            step("Действие: вернуть портретную ориентацию") {
                rotateTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
            step("Проверка: игра доступна, исходное состояние сохранено") {
                ui.game.assertDisplayed()
                ui.saveSnapshot("portrait-preview.png")
                assertEquals(largeBoard.stateBeforeRotation, largeBoard.currentState())
            }
        }
    }

}
