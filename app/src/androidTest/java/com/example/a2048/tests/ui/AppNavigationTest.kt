package com.example.a2048.tests.ui

import androidx.compose.ui.test.onNodeWithTag
import com.example.a2048.TestTags
import com.example.a2048.support.BaseAppTest
import com.example.a2048.support.run
import org.junit.Test

class AppNavigationTest : BaseAppTest() {
    @Test
    fun mainButtonsAreDisplayed() {
        before {
            ui.game.assertDisplayed()
        }.run {
            step("Проверка: все игровые кнопки отображаются") {
                ui.game.assertButtons()
            }
        }
    }

    @Test
    fun recordsReturnByArrow() {
        before {
            ui.game.assertDisplayed()
        }.run {
            step("Действие: открыть рекорды") {
                ui.game.openRecords()
            }
            step("Проверка: открыт экран рекордов") {
                ui.records.assertDisplayed()
            }
            step("Действие: нажать стрелку назад") {
                ui.records.goBack()
            }
            step("Проверка: игра доступна, рекорды закрыты") {
                ui.game.assertDisplayed()
                checkUi { composeRule.onNodeWithTag(TestTags.RECORD_SCREEN).assertDoesNotExist() }
                ui.game.assertButtons()
            }
        }
    }

    @Test
    fun recordsReturnBySystemBack() {
        before {
            ui.game.assertDisplayed()
        }.run {
            step("Действие: открыть рекорды") {
                ui.game.openRecords()
            }
            step("Проверка: открыт экран рекордов") {
                ui.records.assertDisplayed()
            }
            step("Действие: нажать системную кнопку назад") {
                ui.records.goBack(systemBack = true)
            }
            step("Проверка: игра доступна, рекорды закрыты") {
                ui.game.assertDisplayed()
                checkUi { composeRule.onNodeWithTag(TestTags.RECORD_SCREEN).assertDoesNotExist() }
                ui.game.assertButtons()
            }
        }
    }

}
