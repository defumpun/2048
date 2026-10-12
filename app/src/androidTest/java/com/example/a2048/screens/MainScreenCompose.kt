package com.example.a2048.screens

import com.example.a2048.TestTags

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.KNode
import io.github.kakaocup.compose.node.element.ComposeScreen

class MainScreenCompose(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MainScreenCompose>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
            hasTestTag(TestTags.MAIN_SCREEN)
        }
    ) {

    val restartButton: KNode = child { hasTestTag(TestTags.RESTART_BUTTON) }

    val recordButton: KNode = child { hasTestTag(TestTags.RECORD_BUTTON) }

    val changeSizeButton: KNode = child { hasTestTag(TestTags.CHANGE_SIZE_BUTTON) }
}
