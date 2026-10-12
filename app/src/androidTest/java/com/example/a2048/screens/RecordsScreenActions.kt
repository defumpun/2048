package com.example.a2048.screens

import android.content.Context
import androidx.compose.ui.test.click
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.Espresso.pressBack
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import com.example.a2048.R
import com.example.a2048.TestTags
import com.example.a2048.support.checkUi
import com.example.a2048.support.tap
import com.kaspersky.kaspresso.flakysafety.FlakySafetyProvider

class RecordsScreenActions(
    private val rule: ComposeContentTestRule,
    private val safety: FlakySafetyProvider
) {
    fun assertDisplayed() {
        safety.checkUi { rule.onNodeWithTag(TestTags.RECORD_SCREEN).assertIsDisplayed() }
        safety.checkUi { rule.onNodeWithTag(TestTags.MAIN_SCREEN).assertDoesNotExist() }
        val context = ApplicationProvider.getApplicationContext<Context>()
        safety.checkUi {
            onComposeScreen<RecordScreenCompose>(rule) {
                recordText { assertIsDisplayed(); assertTextContains(context.getString(R.string.record_text)) }
            }
        }
    }

    fun goBack(systemBack: Boolean = false) {
        if (systemBack) pressBack() else safety.tap(rule, TestTags.RECORD_BACK_BUTTON)
    }

    fun tapBackRepeatedly(taps: Int = 4) {
        safety.checkUi { rule.onNodeWithTag(TestTags.RECORD_BACK_BUTTON).assertIsDisplayed() }
        rule.onNodeWithTag(TestTags.RECORD_BACK_BUTTON).performTouchInput {
            repeat(taps) { click() }
        }
    }

    fun selectSize(size: Int) {
        val text = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.board_size_tab, size)
        safety.checkUi { rule.onNodeWithText(text).assertIsDisplayed() }
        rule.onNodeWithText(text).performClick()
    }

    fun requestClear() = safety.tap(rule, TestTags.CLEAR_RECORDS_BUTTON)

    fun assertScore(score: Int) {
        val text =
            ApplicationProvider.getApplicationContext<Context>().getString(R.string.points, score)
        safety.checkUi { rule.onNodeWithText(text).assertIsDisplayed() }
    }
}
