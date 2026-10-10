package com.example.a2048

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
open class BaseTest() : TestCase() {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @get:Rule
    val composeTestRule2 = createAndroidComposeRule<ComponentActivity>()

}
