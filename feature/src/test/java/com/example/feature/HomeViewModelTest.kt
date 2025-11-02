package com.example.feature

import app.cash.turbine.test
import com.example.common.PagingModel
import com.example.domain.entity.NewEntity
import com.example.domain.usecase.GetNewsUseCase
import com.example.feature.mapper.NewDomainUiMapper
import com.example.feature.ui.contract.HomeContract
import com.example.feature.ui.viewmodel.HomeViewModel
import com.example.feature.utils.TestDataGenerator
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var getNewsUseCase: GetNewsUseCase

    private val newMapper = NewDomainUiMapper()

    private val newsSource = "bbc-news"
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(dispatcher)
        homeViewModel = HomeViewModel(
            getNewsUseCase = getNewsUseCase,
            newMapper = newMapper,
            newsSourceDisplay = newsSource
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetch data success`() = runTest {
        val newsItems = TestDataGenerator.generateNews()
        val newsFlow = flowOf(newsItems)
        coEvery { getNewsUseCase.execute(1) } returns newsFlow
        homeViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(
                HomeContract.State(HomeContract.HomeState.Idle)
            )
            homeViewModel.setEvent(HomeContract.Event.FetchData)
            assertThat(awaitItem()).isEqualTo(
                HomeContract.State(HomeContract.HomeState.Loading)
            )
            val emitted = awaitItem()
            val success = emitted.homeState as HomeContract.HomeState.Success
            val uiModels = newMapper.fromList(newsItems.data)
            assertThat(success.news)
                .isEqualTo(PagingModel(uiModels, newsItems.total, newsItems.currentPage))
            assertThat(success.title).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { getNewsUseCase.execute(1) }
    }

    @Test
    fun `test fetch data fail`() = runTest {
        val newsFlow = flow<PagingModel<List<NewEntity>>> { throw Exception("error string") }
        coEvery { getNewsUseCase.execute(1) } returns newsFlow
        homeViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(
                HomeContract.State(HomeContract.HomeState.Idle)
            )
            homeViewModel.setEvent(HomeContract.Event.FetchData)
            assertThat(awaitItem()).isEqualTo(
                HomeContract.State(HomeContract.HomeState.Loading)
            )
            cancelAndIgnoreRemainingEvents()
        }
        homeViewModel.effect.test {
            val effect = awaitItem()
            assertThat(effect).isEqualTo(HomeContract.Effect.ShowError("error string"))
            assertThat((effect as HomeContract.Effect.ShowError).message).isEqualTo("error string")
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { getNewsUseCase.execute(1) }
    }

    @Test
    fun `test fetch data success using pull to refresh`() = runTest {
        val newsItems = TestDataGenerator.generateNews()
        val newsFlow = flowOf(newsItems)
        coEvery { getNewsUseCase.execute(1) } returns newsFlow
        homeViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(
                HomeContract.State(HomeContract.HomeState.Idle)
            )
            homeViewModel.setEvent(HomeContract.Event.OnRefresh)
            assertThat(awaitItem()).isEqualTo(
                HomeContract.State(HomeContract.HomeState.Loading)
            )
            val emitted = awaitItem()
            val success = emitted.homeState as HomeContract.HomeState.Success
            val uiModels = newMapper.fromList(newsItems.data)
            assertThat(success.news)
                .isEqualTo(PagingModel(uiModels, newsItems.total, newsItems.currentPage))
            assertThat(success.title).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { getNewsUseCase.execute(1) }
    }

    @Test
    fun `test fetch data success using load more`() = runTest {
        val newsItems = TestDataGenerator.generateNews()
        val newsFlow = flowOf(newsItems)
        val newsItemsPage2 = TestDataGenerator.generateNews(2)
        coEvery { getNewsUseCase.execute(1) } returns newsFlow
        coEvery { getNewsUseCase.execute(2) } returns flowOf(newsItemsPage2)
        homeViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(
                HomeContract.State(HomeContract.HomeState.Idle)
            )
            homeViewModel.setEvent(HomeContract.Event.FetchData)
            assertThat(awaitItem()).isEqualTo(
                HomeContract.State(HomeContract.HomeState.Loading)
            )
            val firstPage = awaitItem()
            val s1 = firstPage.homeState as HomeContract.HomeState.Success
            val uiModels = newMapper.fromList(newsItems.data)
            assertThat(s1.news).isEqualTo(
                PagingModel(uiModels, newsItems.total, newsItems.currentPage)
            )
            assertThat(s1.title).isNotNull()
            homeViewModel.setEvent(HomeContract.Event.LoadMoreData)
            val secondPageState = awaitItem()
            val s2 = secondPageState.homeState as HomeContract.HomeState.Success
            assertThat(s2.news).isEqualTo(
                PagingModel(
                    data = uiModels + uiModels,
                    total = newsItemsPage2.total,
                    currentPage = newsItemsPage2.currentPage
                )
            )
            assertThat(s2.title).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { getNewsUseCase.execute(1) }
        coVerify { getNewsUseCase.execute(2) }
    }

    @Test
    fun test_select_new_item() = runTest {
        val news = TestDataGenerator.generateNews()
        val selectedNewUiModel = newMapper.from(news.data.firstOrNull())
        homeViewModel.event.test {
            homeViewModel.setEvent(HomeContract.Event.NewSelected(newUiModel = selectedNewUiModel))
            assertThat(awaitItem()).isEqualTo(
                HomeContract.Event.NewSelected(selectedNewUiModel)
            )
            cancelAndIgnoreRemainingEvents()
        }
        homeViewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(
                HomeContract.Effect.NavigateToNewDetails(selectedNewUiModel)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
