package com.example.feature.ui.utils


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.feature.model.NewUiModel
import com.example.feature.model.SourceUiModel

@Composable
fun previewPreviewTheme(content: @Composable () -> Unit) {
    val light = lightColorScheme()
    MaterialTheme(colorScheme = light, content = content)
}

fun sampleNew(): NewUiModel = NewUiModel(
    title = "Sample title",
    author = "Author",
    publishedAt = "2025-11-02",
    description = "Short description…",
    content = "Longer content…",
    url = "https://example.com",
    urlToImage = "https://picsum.photos/800/400",
    sourceUiModel = SourceUiModel("Source Name", name = "")
)

fun sampleNewList(): List<NewUiModel> = listOf(sampleNew(), sampleNew().copy(title = "Another one"))
