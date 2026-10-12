package com.example.a2048.screens

import androidx.compose.ui.test.click
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import com.example.a2048.R
import com.example.a2048.TestTags
import com.example.a2048.support.checkUi
import com.example.a2048.support.tap
import com.kaspersky.kaspresso.flakysafety.FlakySafetyProvider

class GameScreenActions(
    private val rule: ComposeContentTestRule,
    private val safety: FlakySafetyProvider
) {
    fun assertDisplayed() =
        safety.checkUi { rule.onNodeWithTag(TestTags.MAIN_SCREEN).assertIsDisplayed() }

    fun assertButtons() = safety.checkUi {
        onComposeScreen<MainScreenCompose>(rule) {
            restartButton { assertIsDisplayed(); assertTextContains(R.string.restart) }
            changeSizeButton { assertIsDisplayed(); assertTextContains(R.string.change_size) }
            recordButton { assertIsDisplayed(); assertTextContains(R.string.record_text_button) }
        }
    }

    fun openRecords() = safety.tap(rule, TestTags.RECORD_BUTTON)

    fun tapOpenRecordsRepeatedly(taps: Int = 4) {
        safety.checkUi { rule.onNodeWithTag(TestTags.RECORD_BUTTON).assertIsDisplayed() }
        rule.onNodeWithTag(TestTags.RECORD_BUTTON).performTouchInput {
            repeat(taps) { click() }
        }
    }

    fun restart() = safety.tap(rule, TestTags.RESTART_BUTTON)

    fun changeSize() = safety.tap(rule, TestTags.CHANGE_SIZE_BUTTON)
}
