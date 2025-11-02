package com.example.common

class RepositoryException(
    val remoteCause: Throwable?,
    val localCause: Throwable?
) : Exception(
    buildString {
        append("Failed to load news.")
        remoteCause?.message?.let { append(" Remote error: ").append(it).append(".") }
        localCause?.message?.let { append(" Local error: ").append(it).append(".") }
    }
)