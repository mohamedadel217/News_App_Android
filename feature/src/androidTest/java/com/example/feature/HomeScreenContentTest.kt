package com.example.feature

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.common.PagingModel
import com.example.feature.model.NewUiModel
import com.example.feature.ui.contract.HomeContract
import com.example.feature.ui.home.HomeScreenContent
import com.example.feature.ui.utils.previewPreviewTheme
import com.example.feature.ui.utils.sampleNewList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class HomeScreenContentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun renders_idle_state() {
        composeRule.setContent {
            previewPreviewTheme {
                HomeScreenContent(
                    state = HomeContract.HomeState.Idle,
                    onLoadMore = {},
                    onItemClick = {}
                )
            }
        }
        composeRule.onNodeWithTag("home_state_idle").assertExists()
    }

    @Test
    fun renders_loading_state() {
        composeRule.setContent {
            previewPreviewTheme {
                HomeScreenContent(
                    state = HomeContract.HomeState.Loading,
                    onLoadMore = {},
                    onItemClick = {}
                )
            }
        }
        composeRule.onNodeWithTag("home_state_loading").assertExists()
    }

    @Test
    fun renders_empty_state() {
        composeRule.setContent {
            previewPreviewTheme {
                HomeScreenContent(
                    state = HomeContract.HomeState.Empty,
                    onLoadMore = {},
                    onItemClick = {}
                )
            }
        }
        composeRule.onNodeWithTag("home_state_empty").assertExists()
        composeRule.onNodeWithText("No items").assertExists()
    }

    @Test
    fun triggers_load_more_on_bottom_reached() {
        val items = (0 until 30).map {
            sampleNewList().first().copy(url = "https://example.com/$it", title = "Item $it")
        }
        val loadMoreCalls = AtomicInteger(0)

        composeRule.setContent {
            previewPreviewTheme {
                HomeScreenContent(
                    state = HomeContract.HomeState.Success(
                        news = PagingModel(items, items.size, 1),
                        title = "Top News"
                    ),
                    onLoadMore = { loadMoreCalls.incrementAndGet() },
                    onItemClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("home_list").performScrollToIndex(items.lastIndex)
        composeRule.waitForIdle()

        assertTrue(loadMoreCalls.get() >= 1)
    }
}
