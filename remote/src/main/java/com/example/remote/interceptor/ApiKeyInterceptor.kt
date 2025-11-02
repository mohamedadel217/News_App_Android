package com.example.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Api-Key", "7b8aafaf16cb464e97e7bf64adba2386")
        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }

}