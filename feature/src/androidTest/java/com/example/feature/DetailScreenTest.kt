package com.example.feature.ui.detail

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import com.example.feature.ui.utils.previewPreviewTheme
import com.example.feature.ui.utils.sampleNew
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

class DetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_title_and_texts_and_back_click_works() {
        val clicked = AtomicBoolean(false)
        composeRule.setContent {
            previewPreviewTheme {
                DetailScreen(
                    newUiModel = sampleNew(),
                    onBackClicked = { clicked.set(true) }
                )
            }
        }

        composeRule.onNodeWithTag("detail_title").assertExists()

        composeRule.onNodeWithTag("detail_title_text").assertExists()

        composeRule.onNodeWithTag("detail_back_btn").assertExists().performClick()

        assertTrue(clicked.get())
    }

    @Test
    fun scrollable_content_is_present() {
        composeRule.setContent {
            previewPreviewTheme {
                DetailScreen(
                    newUiModel = sampleNew(),
                    onBackClicked = {}
                )
            }
        }

        composeRule.onNodeWithTag("detail_scroll").assertExists().performScrollToNode(
            hasTestTag("detail_title_text")
        )
    }
}
