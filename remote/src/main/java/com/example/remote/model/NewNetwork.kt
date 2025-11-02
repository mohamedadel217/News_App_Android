package com.example.remote.model

import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewNetwork(
    @SerialName("source") val source: SourceNetwork? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("urlToImage") val urlToImage: String? = null,
    @SerialName("publishedAt") val publishedAt: String? = null,
    @SerialName("content") val content: String? = null
)
