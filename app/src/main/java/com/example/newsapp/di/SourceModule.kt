package com.example.newsapp.di


import android.content.Context
import com.example.newsapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SourceModule {

    @Provides
    @Singleton
    @Named("newsSource")
    fun provideNewsSource(): String = BuildConfig.NEWS_SOURCE

    @Provides
    @Singleton
    @Named("newsSourceDisplay")
    fun provideNewsSourceDisplay(@ApplicationContext ctx: Context): String =
        ctx.resources.getString(
            ctx.resources.getIdentifier("news_source_name", "string", ctx.packageName)
        )
}