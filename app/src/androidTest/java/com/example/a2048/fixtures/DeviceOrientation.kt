package com.example.a2048.fixtures

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import com.example.a2048.support.AppRule
import com.example.a2048.support.UiTestConfig
import com.kaspersky.kaspresso.flakysafety.FlakySafetyProvider
import org.junit.Assert.assertTrue

object DeviceOrientation {
    fun rotate(rule: AppRule, orientation: Int, safety: FlakySafetyProvider) {
        rule.activityRule.scenario.onActivity { it.requestedOrientation = orientation }
        val landscape = orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        safety.flakySafely(timeoutMs = UiTestConfig.ROTATION_TIMEOUT_MS,
            allowedExceptions = setOf(AssertionError::class.java, IllegalStateException::class.java)) {
            var ready = false
            rule.activityRule.scenario.onActivity { activity ->
                val view = activity.window.decorView
                val expected = if (landscape) Configuration.ORIENTATION_LANDSCAPE else Configuration.ORIENTATION_PORTRAIT
                ready = activity.resources.configuration.orientation == expected &&
                    view.isAttachedToWindow && view.isLaidOut && !view.isLayoutRequested &&
                    (view.width > view.height) == landscape
            }
            assertTrue("Activity window must finish rotating", ready)
        }
    }

    fun restorePortrait(rule: AppRule) {
        rule.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
    }
}
