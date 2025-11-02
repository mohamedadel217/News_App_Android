package com.example.data

import app.cash.turbine.test
import com.example.common.PagingModel
import com.example.common.RepositoryException
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
    fun remote_success_sorted_desc() = runTest {
        val remotePage = unsortedPage()
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
    fun remote_fail_local_success_sorted_desc() = runTest {
        val localPage = unsortedPage()
        coEvery { remoteDataSource.getNews(1, newsSource) } throws IllegalStateException("remote")
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
    fun remote_fail_local_fail_throws_repository_exception() = runTest {
        coEvery { remoteDataSource.getNews(1, newsSource) } throws IllegalStateException("remote boom")
        coEvery { localDataSource.getNews() } throws IllegalArgumentException("local boom")

        newRepository.getNews(1).test {
            val error = awaitError()
            assertThat(error).isInstanceOf(RepositoryException::class.java)
            val re = error as RepositoryException
            assertThat(re.remoteCause).isInstanceOf(IllegalStateException::class.java)
            assertThat(re.localCause).isInstanceOf(IllegalArgumentException::class.java)
            assertThat(re.message).contains("Failed to load news")
            assertThat(re.message).contains("remote boom")
            assertThat(re.message).contains("local boom")
        }
    }

    private fun unsortedPage(): PagingModel<List<NewDTO>> {
        val base = TestDataGenerator.generateNews()
        val items = base.data.toMutableList()
        if (items.size >= 6) {
            items[0] = items[0].copy(publishedAt = "2024-01-03T10:15:30.000Z")
            items[1] = items[1].copy(publishedAt = "2024-01-01T09:00:00.000Z")
            items[2] = items[2].copy(publishedAt = "2024-01-02T12:00:00.000Z")
            items[3] = items[3].copy(publishedAt = "2023-12-31T23:59:59.000Z")
            items[4] = items[4].copy(publishedAt = "2024-01-03T11:00:00.000Z")
            items[5] = items[5].copy(publishedAt = "2024-01-02T00:00:01.000Z")
        }
        return PagingModel(data = items, total = base.total, currentPage = base.currentPage)
    }
}
