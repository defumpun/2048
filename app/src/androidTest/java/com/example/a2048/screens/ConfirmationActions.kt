package com.example.a2048.screens

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.example.a2048.TestTags
import com.example.a2048.support.tap
import com.kaspersky.kaspresso.flakysafety.FlakySafetyProvider

class ConfirmationActions(private val rule: ComposeContentTestRule, private val safety: FlakySafetyProvider) {
    fun confirm() = safety.tap(rule, TestTags.CONFIRM_YES)
    fun cancel() = safety.tap(rule, TestTags.CONFIRM_NO)
}
