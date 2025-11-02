package com.example.domain

import app.cash.turbine.test
import com.example.common.PagingModel
import com.example.domain.entity.NewEntity
import com.example.domain.repository.NewRepository
import com.example.domain.usecase.GetNewsUseCase
import com.example.domain.utils.TestDataGenerator
import com.example.domain.utils.TestDispatcherProvider
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class GetNewsUseCaseTest {

    @MockK
    private lateinit var newRepository: NewRepository

    private lateinit var getNewsUseCase: GetNewsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        getNewsUseCase = GetNewsUseCase(
            newRepository = newRepository,
            dispatcherProvider = TestDispatcherProvider()
        )
    }

    @Test
    fun `test get news success`() = runTest {
        val newsItems = TestDataGenerator.generateNews()
        val newFlow = flowOf(newsItems)

        // Given
        coEvery { newRepository.getNews(1) } returns newFlow

        // When & Then
        val result = getNewsUseCase.execute(params = 1)
        result.test {
            val emitted = awaitItem()
            assertThat(emitted).isEqualTo(newsItems)
            awaitComplete()
        }

        coVerify { newRepository.getNews(1) }
    }

    @Test
    fun `test get news fail`() = runTest {
        val newsFlow = flow<PagingModel<List<NewEntity>>> {
            throw Exception()
        }

        // Given
        coEvery { newRepository.getNews(1) } returns newsFlow

        // When & Then
        val result = getNewsUseCase.execute(1)
        result.test {
            val error = awaitError()
            assertThat(error).isInstanceOf(Exception::class.java)
        }

        coVerify { newRepository.getNews(1) }
    }
}
