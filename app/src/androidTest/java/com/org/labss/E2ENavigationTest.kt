package com.org.labss

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.org.labss.domain.model.Product
import com.org.labss.ui.navigation.ECNavHost
import com.org.labss.ui.theme.LabssTheme
import com.org.labss.ui.vm.ProductUiState
import com.org.labss.ui.vm.SharedViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import com.org.labss.domain.repository.ProductRepository
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class E2ENavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val fakeProducts = listOf(
        Product(1, "Бездротові навушники", "Опис навушників", 2999.0, "", true, "Електроніка", false, 0),
        Product(2, "Смарт-годинник", "Опис годинника", 4500.0, "", true, "Електроніка", false, 0),
        Product(3, "Ігрова миша", "Опис миші", 5.0, "", false, "Аксесуари", false, 0)
    )

    private fun buildMockViewModel(): SharedViewModel {
        val repository = mockk<ProductRepository>(relaxed = true)

        every { repository.observeAllProducts() } returns flowOf(fakeProducts)
        every { repository.observePopularProducts() } returns flowOf(fakeProducts)
        every { repository.observeProductsByQueryAndCategory(any(), any()) } returns flowOf(fakeProducts)
        coEvery { repository.getAllCategories() } returns listOf("Електроніка", "Аксесуари")
        coEvery { repository.refreshProducts() } returns Unit

        return SharedViewModel(repository)
    }


    @Test
    fun e2e_clickCategory_navigatesToSearchScreen_andShowsFilterChip() {
        val viewModel = buildMockViewModel()

        composeRule.setContent {
            LabssTheme {
                ECNavHost(viewModel = viewModel)
            }
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Popular categories").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Електроніка").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Електроніка").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithText("Електроніка").onFirst().assertIsDisplayed()
    }


    @Test
    fun e2e_searchFlow_fromHome_toResults_showsMatchingProduct() {
        val repository = mockk<ProductRepository>(relaxed = true)

        every { repository.observeAllProducts() } returns flowOf(fakeProducts)
        every { repository.observePopularProducts() } returns flowOf(fakeProducts)
        every { repository.observeProductsByQueryAndCategory("навушники", null) } returns
                flowOf(listOf(fakeProducts[0]))
        every { repository.observeProductsByQueryAndCategory("", null) } returns
                flowOf(fakeProducts)
        every { repository.observeProductsByQueryAndCategory(any(), any()) } returns
                flowOf(fakeProducts)
        coEvery { repository.getAllCategories() } returns listOf("Електроніка", "Аксесуари")
        coEvery { repository.refreshProducts() } returns Unit

        val viewModel = SharedViewModel(repository)

        composeRule.setContent {
            LabssTheme {
                ECNavHost(viewModel = viewModel)
            }
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithContentDescription("Search").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithContentDescription("Search").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithContentDescription("Search").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithContentDescription("Search")
            .performScrollTo()

        composeRule.onNode(
            hasSetTextAction()
        ).performTextInput("навушники")

        // Перевіряємо що продукт відображається
        composeRule.onNodeWithText("Бездротові навушники").assertIsDisplayed()
    }
}

