package com.example.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourceNetwork(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null
)