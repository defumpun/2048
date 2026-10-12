package com.example.a2048.support

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.example.a2048.fixtures.TestSnapshots
import com.example.a2048.screens.ConfirmationActions
import com.example.a2048.screens.GameScreenActions
import com.example.a2048.screens.RecordsScreenActions
import com.kaspersky.kaspresso.flakysafety.FlakySafetyProvider

class UiActions(
    private val rule: ComposeContentTestRule,
    private val safety: FlakySafetyProvider,
) {
    val game = GameScreenActions(rule, safety)
    val records = RecordsScreenActions(rule, safety)
    val confirmation = ConfirmationActions(rule, safety)

    fun saveSnapshot(name: String) = TestSnapshots.save(rule, name, safety)

    fun tap(tag: String) = safety.tap(rule, tag)
}
