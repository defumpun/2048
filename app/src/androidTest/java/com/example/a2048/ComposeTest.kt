package com.example.a2048

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.kaspersky.kaspresso.internal.systemscreen.DataUsageSettingsScreen.pressBack
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.utilities.getResourceString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class ComposeTest : BaseTest() {

//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    @get:Rule
//    val rule = createAndroidComposeRule<MainActivity>()

//    @Test
//    fun app_hasComposeRoot() {
//        rule.onRoot().printToLog("UI_TREE")
//    }

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


//    val composeTestRule2 = createComposeRule()
//    @get:Rule

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


//    @Test
//    fun gameOver_isShown_onStart1() {
//        composeTestRule2.setContent {
//            Game2048App(
//                matrix = List(4) { List(4) { 0 } },
//                highlightedCells = emptyList(),
//                size = 4,
//                gameOver = true,
//                currentScore = 123,
//                onMove = {},
//                onChangeSize = {},
//                onOpenRecords = {},
//                onRestart = {},
//                gameOverContent = { score, _ ->
//                    // чистый Compose без AndroidView
//                    Box(Modifier.testTag(TestTags.GAME_OVER_SCREEN)) {
//                        Text("score=$score", Modifier.testTag("score"))
//                    }
//                }
//            )
//        }
//
//        composeTestRule.onNodeWithTag(TestTags.GAME_OVER_SCREEN).assertExists()
//        composeTestRule.onNodeWithTag("score").assertTextEquals("score=123")
//    }
}
