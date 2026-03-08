package com.org.labss

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.org.labss.domain.model.Product
import com.org.labss.domain.repository.ProductRepository
import com.org.labss.ui.vm.ProductEvent
import com.org.labss.ui.vm.SharedViewModel
import io.mockk.MockKMatcherScope
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.collections.filter
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalCoroutinesApi::class)
class SharedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ProductRepository
    private lateinit var viewModel: SharedViewModel

    private val fakeProducts = listOf(
        Product(1, "Навушники", "Опис", 2999.0, "", true, "Електроніка", false, 0),
        Product(2, "Клавіатура", "Опис", 3200.0, "", true, "Аксесуари", false, 0),
        Product(3, "Мишка", "Опис", 1200.0, "", false, "Аксесуари", true, 1)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        every { repository.observePopularProducts() } returns flowOf(fakeProducts.filter { it.isPopular })
        every {
            repository.observeProductsByQueryAndCategory(
                MockKMatcherScope.any(),
                MockKMatcherScope.any()
            )
        } returns flowOf(fakeProducts)
        coEvery { repository.refreshProducts() } returns Unit
        coEvery { repository.getAllCategories() } returns listOf("Електроніка", "Аксесуари")
        viewModel = SharedViewModel(repository)
    }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `initial state isLoading is true`() {
        assertTrue(SharedViewModel(repository).uiState.value.isLoading)
    }

    @Test
    fun `after init popularProducts are loaded`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.popularProducts.isNotEmpty())
    }

    @Test
    fun `after init categories are loaded`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("Електроніка", "Аксесуари"), viewModel.uiState.value.categories)
    }

    @Test
    fun `after init isLoading becomes false`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `OnSearchQueryChanged updates query`() = runTest {
        viewModel.onEvent(ProductEvent.OnSearchQueryChanged("навуш"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("навуш", viewModel.uiState.value.query)
    }

    @Test
    fun `OnSearchQueryChanged with empty clears query`() = runTest {
        viewModel.onEvent(ProductEvent.OnSearchQueryChanged("test"))
        viewModel.onEvent(ProductEvent.OnSearchQueryChanged(""))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("", viewModel.uiState.value.query)
    }

    @Test
    fun `OnCategorySelected updates selectedCategory`() = runTest {
        viewModel.onEvent(ProductEvent.OnCategorySelected("Електроніка"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Електроніка", viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `OnCategorySelected null clears selectedCategory`() = runTest {
        viewModel.onEvent(ProductEvent.OnCategorySelected("Електроніка"))
        viewModel.onEvent(ProductEvent.OnCategorySelected(null))
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `OnAddProductClicked calls repository addProduct`() = runTest {
        viewModel.onEvent(ProductEvent.OnAddProductClicked(1))
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.addProduct(1) }
    }

    @Test
    fun `OnIncreaseQuantity calls repository increaseQuantity`() = runTest {
        viewModel.onEvent(ProductEvent.OnIncreaseQuantity(2))
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.increaseQuantity(2) }
    }

    @Test
    fun `OnDecreaseQuantity calls repository decreaseQuantity`() = runTest {
        viewModel.onEvent(ProductEvent.OnDecreaseQuantity(3))
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.decreaseQuantity(3) }
    }

    @Test
    fun `OnToggleFavorite calls repository toggleFavorite`() = runTest {
        viewModel.onEvent(ProductEvent.OnToggleFavorite(1))
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.toggleFavorite(1) }
    }

    @Test
    fun `uiState emits updated query via Turbine`() = runTest {
        viewModel.uiState.test {
            ReceiveTurbine.awaitItem()
            viewModel.onEvent(ProductEvent.OnSearchQueryChanged("клавіа"))
            testDispatcher.scheduler.advanceUntilIdle()
            val updated = ReceiveTurbine.awaitItem()
            assertEquals("клавіа", updated.query)
            ReceiveTurbine.cancelAndIgnoreRemainingEvents()
        }
    }
}

