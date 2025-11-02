package com.example.feature.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.common.PagingModel
import com.example.feature.model.NewUiModel
import com.example.feature.ui.contract.HomeContract
import com.example.feature.ui.utils.previewPreviewTheme
import com.example.feature.ui.utils.sampleNewList
import com.example.feature.ui.viewmodel.HomeViewModel
import com.example.feature.extension.OnBottomReached

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onItemClick: (NewUiModel) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    if (viewModel.currentState.homeState is HomeContract.HomeState.Idle) {
        viewModel.setEvent(HomeContract.Event.FetchData)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState.homeState as? HomeContract.HomeState.Success)?.title ?: ""
                    Text(title, modifier = Modifier.testTag("home_title"))
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.setEvent(HomeContract.Event.OnRefresh) },
                        modifier = Modifier.testTag("home_refresh_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        HomeScreenContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = uiState.homeState,
            onLoadMore = { viewModel.setEvent(HomeContract.Event.LoadMoreData) },
            onItemClick = onItemClick
        )
    }
}

@Composable
internal fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeContract.HomeState,
    onLoadMore: () -> Unit,
    onItemClick: (NewUiModel) -> Unit
) {
    when (state) {
        is HomeContract.HomeState.Idle -> {
            Box(
                modifier
                    .testTag("home_state_idle")
            )
        }
        is HomeContract.HomeState.Loading -> {
            Box(
                modifier
                    .testTag("home_state_loading")
            ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
        is HomeContract.HomeState.Empty -> {
            Box(
                modifier
                    .testTag("home_state_empty")
            ) {
                Text(
                    text = "No items",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
        is HomeContract.HomeState.Success -> {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = modifier
                    .testTag("home_list")
            ) {
                items(state.news.data, key = { it.url }) { model ->
                    NewCardItem(model) { onItemClick(it) }
                }
            }
            listState.OnBottomReached { onLoadMore() }
        }
    }
}

@Composable
private fun NewCardItem(
    model: NewUiModel,
    onItemClick: (NewUiModel) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { onItemClick(model) }
            .testTag("news_card_${model.url.hashCode()}"),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model.urlToImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "news header",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("news_image")
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = model.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("news_title")
            )
            if (model.author.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = model.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("news_author")
                )
            }
            if (model.publishedAt.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = model.publishedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("news_date")
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Home – Idle")
@Composable
private fun HomeScreenPreview_Idle() {
    previewPreviewTheme {
        HomeScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = HomeContract.HomeState.Idle,
            onLoadMore = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Home – Loading")
@Composable
private fun HomeScreenPreview_Loading() {
    previewPreviewTheme {
        HomeScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = HomeContract.HomeState.Loading,
            onLoadMore = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Home – Empty")
@Composable
private fun HomeScreenPreview_Empty() {
    previewPreviewTheme {
        HomeScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = HomeContract.HomeState.Empty,
            onLoadMore = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Home – Success")
@Composable
private fun HomeScreenPreview_Success() {
    val items = sampleNewList()
    val state = HomeContract.HomeState.Success(
        news = PagingModel(
            data = items,
            total = items.size,
            currentPage = 1
        ),
        title = items.firstOrNull()?.sourceUiModel?.name ?: "Top News"
    )
    previewPreviewTheme {
        HomeScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            onLoadMore = {},
            onItemClick = {}
        )
    }
}
