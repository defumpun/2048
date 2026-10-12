package com.example.a2048.support

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.a2048.MainActivity
import com.example.a2048.fixtures.DeviceOrientation
import com.kaspersky.kaspresso.flakysafety.FlakySafetyProviderSimpleImpl
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.runner.RunWith

typealias AppRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

abstract class BaseUiTest : TestCase(UiTestConfig.kaspressoBuilder()) {
    abstract val composeRule: ComposeContentTestRule

    protected val uiSafety by lazy {
        FlakySafetyProviderSimpleImpl(params.flakySafetyParams, testLogger)
    }

    protected val ui by lazy { UiActions(composeRule, uiSafety) }

    protected fun <T> checkUi(action: () -> T): T = uiSafety.checkUi(action)
}

@RunWith(AndroidJUnit4::class)
abstract class BaseAppTest : BaseUiTest() {
    @get:Rule override val composeRule = createAndroidComposeRule<MainActivity>()

    protected fun rotateTo(orientation: Int) =
        DeviceOrientation.rotate(composeRule, orientation, uiSafety)

    protected fun restorePortrait() = DeviceOrientation.restorePortrait(composeRule)
}

@RunWith(AndroidJUnit4::class)
abstract class BaseComponentTest : BaseUiTest() {
    @get:Rule override val composeRule = createAndroidComposeRule<ComponentActivity>()
}
