package com.example.a2048.support

import com.kaspersky.kaspresso.testcases.core.sections.AfterTestSection
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext

fun <InitData, Data> AfterTestSection<InitData, Data>.run(steps: TestContext<Data>.() -> Unit) {
    after {}.run(steps = steps)
}
