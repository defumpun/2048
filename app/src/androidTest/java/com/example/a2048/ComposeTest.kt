package com.example.a2048

import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Test

class ComposeTest: BaseTest() {

    @Test
    fun textRestartIsDisplayed() = run {
        step("Check restart button is displayed on main screen") {
            ComposeScreen.onComposeScreen<MainScreenCompose>(composeTestRule) {
                flakySafely {
                    restartButton {
                        assertIsDisplayed()
                        assertExists()
                        assertTextContains(R.string.restart)
                    }
                    changeSizeButton {
                        assertIsDisplayed()
                        assertExists()
                        assertTextContains(R.string.change_size)
                    }

                }
            }
        }
    }
}