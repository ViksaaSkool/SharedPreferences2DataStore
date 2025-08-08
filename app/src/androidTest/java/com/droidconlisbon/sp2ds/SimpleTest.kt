package com.droidconlisbon.sp2ds


import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test


class SimpleTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun clickButton_changesText() {
        composeTestRule.waitForIdle()
    }
}