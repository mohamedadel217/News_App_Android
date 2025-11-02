package com.example.remote.model

import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class BaseResponseNetwork(
    @SerialName("status") val status: String? = null,
    @SerialName("totalResults") val totalResults: Int? = 0,
    @SerialName("code") val code: String? = null,
    @SerialName("message") val message: String? = null
)