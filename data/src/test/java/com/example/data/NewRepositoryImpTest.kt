package com.example.data

import app.cash.turbine.test
import com.example.common.PagingModel
import com.example.common.toEpochMillisFromServer
import com.example.data.mapper.NewDataDomainMapper
import com.example.data.model.NewDTO
import com.example.data.repository.LocalDataSource
import com.example.data.repository.NewRepositoryImp
import com.example.data.repository.RemoteDataSource
import com.example.data.utils.TestDataGenerator
import com.example.domain.repository.NewRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class NewRepositoryImpTest {

    @MockK lateinit var remoteDataSource: RemoteDataSource
    @MockK lateinit var localDataSource: LocalDataSource

    private val newMapper = NewDataDomainMapper()
    private lateinit var newRepository: NewRepository

    private val newsSource = "bbc-news"

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        newRepository = NewRepositoryImp(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            newMapper = newMapper,
            newsSource = newsSource
        )
    }

    @Test
    fun `test get news remote success sorted by date desc`() = runTest {
        val remotePage = unsortedRemotePage()
        coEvery { remoteDataSource.getNews(1, newsSource) } returns remotePage

        newRepository.getNews(1).test {
            val emitted = awaitItem()

            val expectedSorted = newMapper.fromList(remotePage.data)
                .sortedByDescending { it.publishedAt.toEpochMillisFromServer() }

            assertThat(emitted).isEqualTo(
                PagingModel(
                    data = expectedSorted,
                    total = remotePage.total,
                    currentPage = 1
                )
            )

            val millis = emitted.data.map { it.publishedAt.toEpochMillisFromServer() }
            assertThat(millis).isEqualTo(millis.sortedDescending())

            awaitComplete()
        }

        coVerify { remoteDataSource.getNews(1, newsSource) }
    }

    @Test
    fun `test get data from local when remote fails and sorted desc`() = runTest {
        val localPage = unsortedLocalPage()
        coEvery { remoteDataSource.getNews(1, newsSource) } throws Exception()
        coEvery { localDataSource.getNews() } returns localPage

        newRepository.getNews(1).test {
            val emitted = awaitItem()

            val expectedSorted = newMapper.fromList(localPage.data)
                .sortedByDescending { it.publishedAt.toEpochMillisFromServer() }

            assertThat(emitted).isEqualTo(
                PagingModel(
                    data = expectedSorted,
                    total = localPage.total,
                    currentPage = 1
                )
            )

            val millis = emitted.data.map { it.publishedAt.toEpochMillisFromServer() }
            assertThat(millis).isEqualTo(millis.sortedDescending())

            awaitComplete()
        }

        coVerify { localDataSource.getNews() }
    }

    @Test
    fun `test get error when remote fail and local fail`() = runTest {
        coEvery { remoteDataSource.getNews(1, newsSource) } throws Exception()
        coEvery { localDataSource.getNews() } throws Exception()

        newRepository.getNews(1).test {
            val error = awaitError()
            assertThat(error).isInstanceOf(Exception::class.java)
        }

        coVerify { remoteDataSource.getNews(1, newsSource) }
    }

    private fun unsortedRemotePage(): PagingModel<List<NewDTO>> {
        val base = TestDataGenerator.generateNews()
        val items = base.data.toMutableList()
        if (items.size >= 3) {
            items[0] = items[0].copy(publishedAt = "2024-01-03T10:15:30.000Z")
            items[1] = items[1].copy(publishedAt = "2024-01-01T09:00:00.000Z")
            items[2] = items[2].copy(publishedAt = "2024-01-02T12:00:00.000Z")
        }
        return PagingModel(data = items, total = base.total, currentPage = base.currentPage)
    }

    private fun unsortedLocalPage(): PagingModel<List<NewDTO>> {
        val base = TestDataGenerator.generateNews()
        val items = base.data.toMutableList()
        if (items.size >= 3) {
            items[0] = items[0].copy(publishedAt = "2024-01-05T00:00:00.000Z")
            items[1] = items[1].copy(publishedAt = "2024-01-03T00:00:00.000Z")
            items[2] = items[2].copy(publishedAt = "2024-01-04T00:00:00.000Z")
        }
        return PagingModel(data = items, total = base.total, currentPage = base.currentPage)
    }
}
