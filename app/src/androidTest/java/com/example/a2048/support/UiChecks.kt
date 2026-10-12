package com.example.a2048.support

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.kaspersky.kaspresso.flakysafety.FlakySafetyProvider

fun <T> FlakySafetyProvider.checkUi(action: () -> T): T =
    flakySafely(action = action)

fun FlakySafetyProvider.tap(rule: ComposeContentTestRule, tag: String) {
    checkUi { rule.onNodeWithTag(tag).assertIsDisplayed().assertIsEnabled() }
    rule.onNodeWithTag(tag).performClick()
}
