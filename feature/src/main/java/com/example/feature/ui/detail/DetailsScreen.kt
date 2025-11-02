package com.example.feature.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feature.model.NewUiModel
import com.example.feature.ui.utils.previewPreviewTheme
import com.example.feature.ui.utils.sampleNew

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    newUiModel: NewUiModel,
    onBackClicked: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackClicked,
                        modifier = Modifier.testTag("detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                title = {
                    Text(
                        text = newUiModel.sourceUiModel.name,
                        modifier = Modifier.testTag("detail_title")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .testTag("detail_scroll"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(newUiModel.urlToImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "news header",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = newUiModel.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.testTag("detail_title_text")
            )

            if (newUiModel.author.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = newUiModel.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            if (newUiModel.publishedAt.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = newUiModel.publishedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            if (newUiModel.description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = newUiModel.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (newUiModel.content.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = newUiModel.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Detail – Default")
@Composable
private fun DetailScreenPreview_Default() {
    previewPreviewTheme {
        DetailScreen(newUiModel = sampleNew(), onBackClicked = {})
    }
}