package com.example.a2048

import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.builder.ViewBuilder
import io.github.kakaocup.compose.node.element.KNode
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.kakao.common.utilities.getResourceString

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

    fun assertDisplayed() = apply {
        assertIsDisplayed()
    }


}

fun ViewBuilder.hasTextId(@StringRes stringId: Int) =
    this.hasText(getResourceString(stringId))
