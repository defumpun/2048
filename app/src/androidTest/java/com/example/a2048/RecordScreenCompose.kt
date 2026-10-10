package com.example.a2048

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTextExactly
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.github.kakaocup.kakao.common.utilities.getResourceString


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

    val recordText2 = child<KNode> {
        hasTestTag(TestTags.RECORD_TITLE)
    }

}