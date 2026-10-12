package com.example.a2048.tests.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import com.example.a2048.TestTags
import com.example.a2048.support.BaseAppTest
import com.example.a2048.support.run
import org.junit.Test

class NavigationStressTest : BaseAppTest() {
    @Test
    fun repeatedBackTapsCannotRemoveGameScreen() {
        before {
            ui.game.assertDisplayed()
            ui.game.openRecords()
        }.run {
            step("Проверка: открыт экран рекордов") {
                ui.records.assertDisplayed()
            }
            step("Действие: быстро нажать назад четыре раза") {
                ui.records.tapBackRepeatedly(4)
            }
            step("Проверка: игра и кнопка рекордов доступны") {
                ui.game.assertDisplayed()
                checkUi { composeRule.onNodeWithTag(TestTags.RECORD_BUTTON).assertIsDisplayed() }
            }
        }
    }

    @Test
    fun repeatedOpenTapsCreateOnlyOneRecordsScreen() {
        before {
            ui.game.assertDisplayed()
        }.run {
            step("Действие: быстро открыть рекорды четырьмя нажатиями") {
                ui.game.tapOpenRecordsRepeatedly(4)
            }
            step("Проверка: открыт экран рекордов") {
                ui.records.assertDisplayed()
            }
            step("Действие: вернуться одним нажатием назад") {
                ui.records.goBack()
            }
            step("Проверка: сразу открылась игра") {
                ui.game.assertDisplayed()
            }
        }
    }

    @Test
    fun repeatedRoundTripsKeepGameAndControlsAvailable() {
        before {
            ui.game.assertDisplayed()
        }.run {
            repeat(12) { index ->
                step("Действие: открыть рекорды, проход ${index + 1}") {
                    ui.game.openRecords()
                }
                step("Проверка: рекорды доступны, проход ${index + 1}") {
                    ui.records.assertDisplayed()
                }
                step("Действие: нажать назад три раза, проход ${index + 1}") {
                    ui.records.tapBackRepeatedly(3)
                }
                step("Проверка: игра доступна, проход ${index + 1}") {
                    ui.game.assertDisplayed()
                }
            }
            step("Действие: запросить изменение размера поля") {
                ui.game.changeSize()
            }
            step("Проверка: главный экран остаётся доступен") {
                ui.game.assertDisplayed()
            }
        }
    }

}
