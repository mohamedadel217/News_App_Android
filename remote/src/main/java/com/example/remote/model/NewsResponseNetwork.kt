package com.example.remote.model

import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponseNetwork(
    @SerialName("articles") val articles: List<NewNetwork>? = null
) : BaseResponseNetwork()
