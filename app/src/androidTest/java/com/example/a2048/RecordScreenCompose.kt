package com.example.a2048

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode


class RecordScreenCompose(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RecordScreenCompose>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
            hasTestTag(TestTags.RECORD_SCREEN)
        }
    ) {

    val recordText: KNode = child<KNode> {
        useUnmergedTree = true
        hasTestTag(TestTags.RECORD_TITLE)
    }


}
