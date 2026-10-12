package com.example.a2048.support

import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.params.FlakySafetyParams

object UiTestConfig {
    const val UI_TIMEOUT_MS = 5_000L
    const val ROTATION_TIMEOUT_MS = 10_000L
    const val RETRY_INTERVAL_MS = 100L

    fun kaspressoBuilder() = Kaspresso.Builder.simple {
        flakySafetyParams = FlakySafetyParams.custom(
            timeoutMs = UI_TIMEOUT_MS,
            intervalMs = RETRY_INTERVAL_MS,
        )
    }
}
