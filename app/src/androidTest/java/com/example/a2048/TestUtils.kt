package com.example.a2048

import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class TestUtils {
    fun <T : ComposeScreen<T>> assertElementContainsText(
        screen: T,
        node: KNode,
        expected: String,
        rootShouldBeDisplayed: Boolean = true
    ): T = screen.apply {
        if (rootShouldBeDisplayed) {
            assertIsDisplayed()
        }
        node {
            assertIsDisplayed()
            assertTextContains(expected)
        }
    }
}
