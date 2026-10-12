package com.example.a2048.tests.integration

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.example.a2048.support.run
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApplicationContextTest : TestCase() {
    @Test
    fun applicationPackageIsCorrect() {
        lateinit var context: Context

        before {
            context = InstrumentationRegistry.getInstrumentation().targetContext
        }.run {
            step("Проверка: имя пакета приложения корректно") {
                assertEquals("com.example.a2048", context.packageName)
            }
        }
    }

}
