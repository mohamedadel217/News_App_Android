package com.example.newsapp.di

import com.example.common.dispatcher.AppDispatcherProvider
import com.example.common.dispatcher.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


/**
 * Module that holds Dispatchers
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    internal fun providesAppDispatcherProvider(): DispatcherProvider = AppDispatcherProvider()
}