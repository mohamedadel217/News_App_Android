package com.example.feature

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.feature.ui.login.LoginScreen
import com.example.feature.ui.utils.previewPreviewTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_authenticating_placeholder() {
        composeRule.setContent {
            previewPreviewTheme {
                LoginScreen(onNavigate = {}, onClose = {})
            }
        }
        composeRule.onNodeWithText("Authenticating…").assertExists()
    }
}
