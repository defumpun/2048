package com.example.a2048

import androidx.compose.ui.test.onAllNodesWithTag
import com.kaspersky.kaspresso.internal.systemscreen.DataUsageSettingsScreen.pressBack
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.utilities.getResourceString
import org.junit.Test

class ComposeTest : BaseTest() {

    @Test
    fun textRestartIsDisplayed() =
        run {
            composeTestRule.waitUntil(5_000) {
                composeTestRule.onAllNodesWithTag(TestTags.MAIN_SCREEN)
                    .fetchSemanticsNodes().isNotEmpty()
            }
            step("Check restart button is displayed on main screen") {
                onComposeScreen<MainScreenCompose>(composeTestRule) {
                    flakySafely {
                        restartButton {
                            assertExists()
                            assertIsDisplayed()
                            assertTextContains(R.string.restart)
                        }
                        changeSizeButton {
                            assertExists()
                            assertIsDisplayed()
                            assertTextContains(R.string.change_size)
                        }
                        recordButton {
                            assertExists()
                            assertIsDisplayed()
                            assertTextContains(R.string.record_text_button)
                        }

                    }
                }
            }
        }

    @Test
    fun textClickRecords() = run {
        step("Click Records") {
            flakySafely {
                with(composeTestRule) {
                    waitForIdle()
                    onComposeScreen<MainScreenCompose>(this) {
                        recordButton
                            .performClick()
                    }
                }
            }
        }
        step("Check Records Screen and return to main screen by click on screen") {
            flakySafely {
                with(composeTestRule) {
                    waitForIdle()
                    onComposeScreen<RecordScreenCompose>(this) {
                        assertExists()
                        recordText {
                            assertExists()
                            assertTextContains(getResourceString(R.string.record_text))
                            performClick()
                        }
                    }
                }
            }
        }
        step("Click Records again") {
            flakySafely {
                with(composeTestRule) {
                    waitForIdle()
                    onComposeScreen<MainScreenCompose>(this) {
                        assertDisplayed()
                        recordButton
                            .performClick()
                    }
                }
            }
        }
        step("Check Records Screen") {
            flakySafely {
                with(composeTestRule) {
                    waitForIdle()
                    onComposeScreen<RecordScreenCompose>(this) {
                        assertExists()
                        recordText {
                            assertExists()
                            assertTextContains(getResourceString(R.string.record_text))
                            performClick()
                        }
                    }
                }
            }
        }
        step("Return to main screen by press back") {
            pressBack()
        }
        step("Check pressBack is success") {
            flakySafely {
                with(composeTestRule) {
                    waitForIdle()
                    onComposeScreen<MainScreenCompose>(composeTestRule) {
                        recordButton {
                            assertExists()
                            assertIsDisplayed()
                            assertTextContains(R.string.record_text_button)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun gameOver_isShown_onStart() = run {
        composeTestRule2.setContent {
            Game2048App(
                matrix = listOf(
                    listOf(0, 0, 0, 0),
                    listOf(0, 0, 0, 0),
                    listOf(0, 0, 0, 0),
                    listOf(0, 0, 0, 0),
                ),
                highlightedCells = emptyList(),
                size = 4,
                gameOver = true,
                currentScore = 123,
                onMove = {},
                onChangeSize = {},
                onOpenRecords = {},
                onRestart = {}
            )
        }
        step("Check restart button is displayed on main screen") {
            onComposeScreen<GameOverScreenCompose>(composeTestRule) {
                flakySafely {
                    restartButton {
                        assertExists()
                        assertIsDisplayed()
                        assertTextContains(R.string.restart)
                    }

                }
            }
        }
    }

}
