package com.example.a2048

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GameOverScreenCompose (semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GameOverScreenCompose>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag(TestTags.GAME_OVER_SCREEN) }
    ) {
    // не работает потому что там не компоуз

    val restartButton: KNode = child {
        hasTextId(R.string.restart)
    }


}