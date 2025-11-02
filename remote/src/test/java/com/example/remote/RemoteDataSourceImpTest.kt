package com.example.remote

import com.example.common.PagingModel
import com.example.data.repository.RemoteDataSource
import com.example.remote.api.ApiService
import com.example.remote.mapper.NewNetworkDataMapper
import com.example.remote.source.RemoteDataSourceImp
import com.example.remote.utils.TestDataGenerator
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RemoteDataSourceImpTest {

    @MockK
    private lateinit var apiService : ApiService
    private val newNetworkDataMapper = NewNetworkDataMapper()

    private lateinit var remoteDataSource : RemoteDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true) // turn relaxUnitFun on for all mocks
        // Create RemoteDataSourceImp before every test
        remoteDataSource = RemoteDataSourceImp(
            apiService = apiService,
            newMapper = newNetworkDataMapper,
        )
    }

    @Test
    fun `test get news success`() = runTest {

        val newsNetwork = TestDataGenerator.generateNews()

        // Given
        coEvery { apiService.getNews(page = 1, sources = "bbc") } returns newsNetwork

        // When
        val result = remoteDataSource.getNews(1,"bbc")

        // Then
        coVerify { apiService.getNews(page = 1, sources = "bbc") }

        // Assertion
        val list = newNetworkDataMapper.fromList(newsNetwork.articles)
        val expected = PagingModel(list, currentPage = 1, total = newsNetwork.totalResults ?: 0)
        Truth.assertThat(result).isEqualTo(expected)
    }

    @Test(expected = Exception::class)
    fun `test get news fail`() = runTest {

        // Given
        coEvery { apiService.getNews(page = 1, sources = "bbc") } throws Exception()

        // When
        remoteDataSource.getNews(page = 1,"bbc")

        // Then
        coVerify { apiService.getNews(page = 1, sources = "bbc") }

    }

}